package com.example.mygame.Pole

// Добавьте эти импорты:

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.mygame.GlobalParam.GlobalDxKoef
import com.example.mygame.R
import com.example.mygame.Terrain
import com.example.mygame.PackageForKoor.koorOnInt
import com.example.mygame.PackageForKoor.limitation


class Pole(
    private val context: Context,
) {
    // Создаём изменяемый двумерный массив
    var mass: MutableList<MutableList<Cell>> = mutableListOf( )

    // Создаём изменяемый двумерный массив
    val image_mass: MutableList<Int> = mutableListOf(
        R.drawable.hex_grey,     //0
        R.drawable.hex_cream,    //1
        R.drawable.hex_blue,     //2
        R.drawable.hex_green,    //3
        R.drawable.hex_brown,    //4
        R.drawable.hex_cyan,     //5
        R.drawable.hex_red,      //6
        R.drawable.hex_yellow,   //7
        R.drawable.hex_purple,   //8
    )
    private val landDrawables = listOf(
        R.drawable.land_meadow,
        R.drawable.land_flowers,
        R.drawable.land_wheat,
        R.drawable.land_palms,
        R.drawable.land_bushes,
    )
    private val shoreDrawables = listOf(
        R.drawable.edge_water_n,
        R.drawable.edge_water_ne,
        R.drawable.edge_water_se,
        R.drawable.edge_water_s,
        R.drawable.edge_water_sw,
        R.drawable.edge_water_nw,
    )
    private val ownerColors = listOf(
        Color(0xFF7F7F7F),
        Color(0xFFEFE4B0),
        Color(0xFF3F48CC),
        Color(0xFF22B14C),
        Color(0xFFB97A57),
        Color(0xFF00A2E8),
        Color(0xFFED1C24),
        Color(0xFFFFF200),
        Color(0xFFA349A4),
    )
    val display = koorOnInt(
        context.resources.displayMetrics.widthPixels,
        context.resources.displayMetrics.heightPixels
    )
    val ogranichenie = limitation(
        koorOnInt(0, (display.y * 0.13).toInt()),
        koorOnInt((display.x * 0.8).toInt(), (display.y - display.y * 0.1).toInt())
    )

    // Делаем координаты наблюдаемыми состояниями
    var poleX by mutableStateOf(0)
    var poleY by mutableStateOf(ogranichenie.min.y)
    var dx by mutableStateOf(0)

    var minDx = 0
    var maxDx = display.y*0.1 //300
    private var previousTouchX = 0
    private var previousTouchY = 0
    private var isDragging = false

    private var initialX2 = 0
    private var initialY2 = 0
    private var touchState by mutableStateOf(TouchState())

    var biomeSeed: Long = 0
        private set
    private var biomeCenters: List<Pair<Float, Float>> = emptyList()

    init {
        initializePole()
    }

    private fun initializePole() {
        mass = createCellMatrix(11,17)
        calculateHexagonWidth( )
        applyBiomeSeed(0L)
    }

    fun applyBiomeSeed(seed: Long) {
        var resolved = seed
        if (resolved == 0L) {
            resolved = Random.nextLong()
            if (resolved == 0L) resolved = 1L
        }
        biomeSeed = resolved
        val cols = mass.size
        val rows = if (mass.isEmpty()) 0 else mass[0].size
        biomeCenters = placeBiomeCenters(cols, rows, landDrawables.size, Random(resolved))
        paintIsland(resolved)
    }

    private fun paintIsland(seed: Long) {
        val cols = mass.size
        val rows = if (mass.isEmpty()) 0 else mass[0].size
        if (cols == 0 || rows == 0) return
        val noise = smoothedNoise(cols, rows, seed)
        val total = cols * rows
        var low = 0.45f
        var high = 1.25f
        var chosen = islandMask(cols, rows, noise, 0.9f)
        for (step in 0 until 8) {
            val mid = (low + high) / 2f
            val mask = islandMask(cols, rows, noise, mid)
            val landCount = mask.sumOf { column -> column.count { it } }
            val fraction = landCount / total.toFloat()
            chosen = mask
            if (fraction in 0.55f..0.70f) break
            if (fraction < 0.55f) low = mid else high = mid
        }
        for (i in mass.indices) {
            for (j in mass[i].indices) {
                mass[i][j].land = if (chosen[i][j]) Terrain.LAND else Terrain.WATER
            }
        }
    }

    private fun smoothedNoise(cols: Int, rows: Int, seed: Long): Array<FloatArray> {
        val raw = Array(cols) { i ->
            FloatArray(rows) { j -> hashNoise(i, j, seed) }
        }
        return Array(cols) { i ->
            FloatArray(rows) { j ->
                var sum = raw[i][j]
                val neighbors = hexNeighbors(i, j, cols, rows)
                for ((nx, ny) in neighbors) sum += raw[nx][ny]
                sum / (neighbors.size + 1)
            }
        }
    }

    private fun hashNoise(i: Int, j: Int, seed: Long): Float {
        var n = seed xor (i.toLong() * 374761393L) xor (j.toLong() * 668265263L)
        n = n xor (n ushr 30)
        n *= 0xBF58476D1CE4E5B9uL.toLong()
        n = n xor (n ushr 27)
        n *= 0x94D049BB133111EBuL.toLong()
        n = n xor (n ushr 31)
        return ((n ushr 40) and 0xFFFFFF) / 0xFFFFFF.toFloat()
    }

    private fun islandMask(
        cols: Int,
        rows: Int,
        noise: Array<FloatArray>,
        threshold: Float
    ): Array<BooleanArray> {
        val halfX = ((cols - 1) / 2f).coerceAtLeast(1f)
        val halfY = ((rows - 1) / 2f).coerceAtLeast(1f)
        var mask = Array(cols) { i ->
            BooleanArray(rows) { j ->
                val nx = (i - (cols - 1) / 2f) / halfX
                val ny = (j - (rows - 1) / 2f) / halfY
                val radial = sqrt(nx * nx + ny * ny)
                val wobble = (noise[i][j] - 0.5f) * 0.55f
                radial + wobble < threshold
            }
        }
        repeat(2) { mask = smoothIsland(mask) }
        if (cols > 0 && rows > 0) {
            mask[0][0] = false
            mask[0][rows - 1] = false
            mask[cols - 1][0] = false
            mask[cols - 1][rows - 1] = false
        }
        return largestComponent(mask)
    }

    private fun smoothIsland(mask: Array<BooleanArray>): Array<BooleanArray> {
        val cols = mask.size
        val rows = mask[0].size
        val next = Array(cols) { i -> mask[i].copyOf() }
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                var landNeighbors = 0
                for ((nx, ny) in hexNeighbors(i, j, cols, rows)) {
                    if (mask[nx][ny]) landNeighbors++
                }
                next[i][j] = if (mask[i][j]) landNeighbors >= 2 else landNeighbors >= 5
            }
        }
        return next
    }

    private fun largestComponent(mask: Array<BooleanArray>): Array<BooleanArray> {
        val cols = mask.size
        val rows = if (cols == 0) 0 else mask[0].size
        val seen = Array(cols) { BooleanArray(rows) }
        var best = emptyList<Pair<Int, Int>>()
        for (i in 0 until cols) {
            for (j in 0 until rows) {
                if (!mask[i][j] || seen[i][j]) continue
                val stack = ArrayDeque<Pair<Int, Int>>()
                val component = mutableListOf<Pair<Int, Int>>()
                stack.add(i to j)
                seen[i][j] = true
                while (stack.isNotEmpty()) {
                    val (x, y) = stack.removeLast()
                    component.add(x to y)
                    for ((nx, ny) in hexNeighbors(x, y, cols, rows)) {
                        if (mask[nx][ny] && !seen[nx][ny]) {
                            seen[nx][ny] = true
                            stack.add(nx to ny)
                        }
                    }
                }
                if (component.size > best.size) best = component
            }
        }
        val result = Array(cols) { BooleanArray(rows) }
        for ((x, y) in best) result[x][y] = true
        return result
    }

    private fun hexNeighbors(x: Int, y: Int, cols: Int, rows: Int): List<Pair<Int, Int>> {
        val candidates = arrayListOf(
            x - 1 to y,
            x to y - 1,
            x + 1 to y,
            x to y + 1
        )
        if (x % 2 == 0) {
            candidates.add(x - 1 to y - 1)
            candidates.add(x + 1 to y - 1)
        } else {
            candidates.add(x - 1 to y + 1)
            candidates.add(x + 1 to y + 1)
        }
        return candidates.filter { (nx, ny) -> nx in 0 until cols && ny in 0 until rows }
    }

    fun createCellMatrix(width: Int, height: Int): MutableList<MutableList<Cell>> {
        return MutableList(width) { row ->
            MutableList(height) { col ->
                Cell(
                    player = null,
                    protection = 0,
                    land = Terrain.LAND
                )
            }
        }
    }

    fun calculateHexagonWidth() {
        val screenX =ogranichenie.max.x - ogranichenie.min.x
        dx = screenX/mass.size
        for (i in 1..500){
            val d =dx +i
            val k = ((mass.size-1) * (d * GlobalDxKoef))+d
            if (k >=ogranichenie.max.x){
                dx = d-1
                break
            }
        }
        minDx=dx

        ogranichenie.min.y=poleY
        ogranichenie.max.y=ogranichenie.min.y+(mass[0].size)*minDx+minDx/2
        ogranichenie.min.x=((display.x-screenX)/2)
        ogranichenie.max.x=ogranichenie.min.x+screenX

        poleX=ogranichenie.min.x
        poleY=ogranichenie.min.y
    }

    @Composable
    fun Render() {
        //Log.d("pole","$dx")
        drawHexagonPole() // рисует поле из шестиугольников
        /*
        LaunchedEffect(mass) {
            Log.d("PoleRender", "=== Mass content ===")
            for (i in mass.indices) {
                for (j in mass[i].indices) {
                    val cell = mass[i][j]
                    Log.d("PoleRender", "mass[$i][$j]: player=${cell.player?.color}, protection=${cell.protection}, land=${cell.land}")
                }
            }
        }*/

        //drawRectPole()// рисует поле из квадратов

        /*
        rect(modifier = Modifier,
            ogranichenie.min.x.toFloat(),
            ogranichenie.min.y.toFloat(),
            ((ogranichenie.max.x - ogranichenie.min.x).toFloat()),
            ((ogranichenie.max.y - ogranichenie.min.y).toFloat()), 5f, Color.Blue)
        */
    }

    @Composable
    fun rect(modifier: Modifier = Modifier, x: Float, y: Float, dx: Float, dy: Float, l: Float, color: Color) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                start = Offset(x, y),
                end = Offset(x, y + dy),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x, y + dy),
                end = Offset(x + dx, y + dy),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x + dx, y + dy),
                end = Offset(x + dx, y),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x + dx, y),
                end = Offset(x, y),
                color = color,
                strokeWidth = l
            )
        }
    }

    fun onTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // начало касания
                previousTouchX = event.x.toInt()
                previousTouchY = event.y.toInt()
                isDragging = true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Второе и последующие касания
                val pointerIndex = event.actionIndex
                if (pointerIndex == 1) {
                    initialX2 = event.getX(1).toInt()
                    initialY2 = event.getY(1).toInt()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    val currentX1 = event.getX(0).toInt()
                    val currentY1 = event.getY(0).toInt()
                    val currentX2 = event.getX(1).toInt()
                    val currentY2 = event.getY(1).toInt()

                    var distance0 = ""

                    val dist = distanceBetweenPoints(currentX1, currentY1, currentX2, currentY2)

                    val k = 2
                    var newX = poleX
                    var newY = poleY
                    var newdx: Int = touchState.newdX

                    // Уменьшение
                    if ((dist - touchState.dist < 0 && dx - k >= minDx)) {
                        newdx = dx - k
                        val old_x = mass.size*(dx*GlobalDxKoef)
                        val new_x = mass.size*(newdx*GlobalDxKoef)
                        var delta = (old_x-new_x).toInt()
                        newX = (poleX+delta/2)

                        val old_y = (mass[0].size*dx+dx*0.5).toInt()
                        val new_y = (mass[0].size*newdx+newdx*0.5).toInt()
                        delta=old_y-new_y
                        newY = (poleY+delta/2)
                    //Увеличение
                    }else if ((dist - touchState.dist > 0 && dx+k<=maxDx)) {
                        newdx = dx + k
                        val old_x=mass.size*(dx*GlobalDxKoef)
                        val new_x=mass.size*(newdx*GlobalDxKoef)
                        var delta = new_x-old_x
                        newX = (poleX-delta/2).toInt()

                        val old_y = mass[0].size*dx+dx*0.5
                        val new_y = mass[0].size*newdx+newdx*0.5
                        delta=new_y-old_y
                        newY = (poleY-delta/2).toInt()

                    }else {
                        touchState = TouchState(
                            do_deltaX1 = currentX1,
                            do_deltaY1 = currentY1,
                            do_deltaX2 = currentX2,
                            do_deltaY2 = currentY2,
                            scale = distance0,
                            dist = dist,
                            )
                        return
                    }

                    dx = newdx
                    val end_pole_x = newX+((mass.size-1) * (dx * GlobalDxKoef))+dx*GlobalDxKoef
                    val end_pole_y = newY+((mass[0].size) *dx + (dx /2))

                    if (newX>=ogranichenie.min.x){
                        poleX=ogranichenie.min.x
                    } else if (end_pole_x<ogranichenie.max.x) {
                        val delta=ogranichenie.max.x-end_pole_x
                        poleX= (newX+delta).toInt()
                    } else {
                        poleX=newX
                    }

                    if (newY>=ogranichenie.min.y){
                        poleY=ogranichenie.min.y
                    } else if (end_pole_y<ogranichenie.max.y) {
                        val delta=ogranichenie.max.y-end_pole_y
                        poleY=newY+delta
                    } else {
                        poleY=newY
                    }

                    touchState = TouchState(
                        do_deltaX1 = currentX1,
                        do_deltaY1 = currentY1,
                        do_deltaX2 = currentX2,
                        do_deltaY2 = currentY2,
                        scale = distance0,
                        dist = dist,
                        )
                }

                if (isDragging && event.pointerCount == 1) {
                    val currentX = event.x.toInt()
                    val currentY = event.y.toInt()

                    // Вычисляем смещение
                    val deltaX = currentX - previousTouchX
                    val deltaY = currentY - previousTouchY

                    // Обновляем позицию поля
                    val newX = poleX + deltaX.toInt()
                    val newDxX =  poleX+mass.size*(dx*GlobalDxKoef)+deltaX+dx*0.3

                    val newY = poleY + deltaY.toInt()
                    val newDyY = poleY + mass[0].size*dx+dx/2+deltaY

                    // Ограничиваем движение в пределах границ
                    if (newX < ogranichenie.min.x && newDxX > ogranichenie.max.x) {
                        poleX = newX
                    }
                    if (newY < ogranichenie.min.y && newDyY > ogranichenie.max.y) {
                        poleY = newY
                    }

                    // Сохраняем текущие координаты для следующего движения
                    previousTouchX = currentX
                    previousTouchY = currentY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                isDragging = false
                // Сброс координат при отпускании
                if (event.pointerCount <= 1) {
                    initialX2 = 0
                    initialY2 = 0
                    previousTouchX=0
                    previousTouchY=0
                }
                touchState = TouchState()
            }
        }
    }

    fun distanceBetweenPoints(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val deltaX = x2 - x1
        val deltaY = y2 - y1

        return sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toInt()
    }

    private fun placeBiomeCenters(
        cols: Int,
        rows: Int,
        count: Int,
        random: Random
    ): List<Pair<Float, Float>> {
        if (cols <= 0 || rows <= 0 || count <= 0) return emptyList()
        val minDist = min(cols, rows) / 3f
        val points = mutableListOf<Pair<Float, Float>>()
        repeat(80) {
            if (points.size >= count) return points
            val candidate = Pair(random.nextFloat() * (cols - 1), random.nextFloat() * (rows - 1))
            if (points.all { biomeDistance(it, candidate) >= minDist }) {
                points.add(candidate)
            }
        }
        while (points.size < count) {
            points.add(Pair(random.nextFloat() * (cols - 1), random.nextFloat() * (rows - 1)))
        }
        return points
    }

    private fun biomeDistance(a: Pair<Float, Float>, b: Pair<Float, Float>): Float {
        val dx = (a.first - b.first) * 0.74f
        val dy = a.second - b.second
        return sqrt(dx * dx + dy * dy)
    }

    private fun biomeWeights(i: Int, j: Int): FloatArray {
        if (biomeCenters.isEmpty()) {
            return FloatArray(landDrawables.size) { index -> if (index == 0) 1f else 0f }
        }
        return FloatArray(biomeCenters.size) { index ->
            val center = biomeCenters[index]
            val dx = (i - center.first) * 0.74f
            val dy = j - center.second
            val d2 = dx * dx + dy * dy
            1f / (d2 + 0.35f)
        }
    }

    private data class BiomeBlend(val first: Int, val second: Int, val secondAlpha: Float)

    private fun biomeBlend(i: Int, j: Int): BiomeBlend {
        val weights = biomeWeights(i, j)
        var first = 0
        var second = -1
        for (k in 1 until weights.size) {
            if (weights[k] > weights[first]) {
                second = first
                first = k
            } else if (second == -1 || weights[k] > weights[second]) {
                second = k
            }
        }
        val lead = weights[first]
        val other = if (second >= 0) weights[second] else 0f
        val alpha = if (lead > 0f && other >= lead * 0.4f) other / (lead + other) else 0f
        return BiomeBlend(first, second, alpha)
    }

    private fun sideOffset(x: Int, side: Int): Pair<Int, Int> {
        val odd = x % 2 != 0
        return when (side) {
            0 -> 0 to -1
            1 -> if (odd) 1 to 0 else 1 to -1
            2 -> if (odd) 1 to 1 else 1 to 0
            3 -> 0 to 1
            4 -> if (odd) -1 to 1 else -1 to 0
            else -> if (odd) -1 to 0 else -1 to -1
        }
    }

    private fun neighborIsWater(x: Int, y: Int, side: Int): Boolean {
        val (dxn, dyn) = sideOffset(x, side)
        val nx = x + dxn
        val ny = y + dyn
        if (nx !in mass.indices || mass.isEmpty() || ny !in mass[nx].indices) return true
        return mass[nx][ny].land == Terrain.WATER
    }

    private fun flatTopHexPath(x: Float, y: Float, size: Float): Path {
        return Path().apply {
            moveTo(x + size * 0.25f, y)
            lineTo(x + size * 0.75f, y)
            lineTo(x + size, y + size * 0.5f)
            lineTo(x + size * 0.75f, y + size)
            lineTo(x + size * 0.25f, y + size)
            lineTo(x, y + size * 0.5f)
            close()
        }
    }

    @Composable
    fun drawHexagonPole(){
        val context = LocalContext.current
        val meadowBitmap = remember {
            BitmapFactory.decodeResource(context.resources, R.drawable.land_meadow).asImageBitmap()
        }
        val shoreBitmaps = remember {
            shoreDrawables.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in mass.indices) {
                for (j in mass[i].indices) {
                        val cell = mass[i][j]
                        if (cell.land == Terrain.WATER) {
                            continue
                        }
                        var l = 0
                        if (i % 2 != 0) {
                            l = (dx/2)
                        }
                        val posX= (poleX + i * (dx * GlobalDxKoef)).toInt()
                        val posY= (poleY + j * (dx) + l).toInt()

                        if (posX + dx > 0 && posX < display.x &&
                            posY + dx > 0 && posY < display.y
                        ) {
                            val cellSize = dx.toInt()
                            if (cellSize <= 0) {
                                continue
                            }
                            val path = flatTopHexPath(posX.toFloat(), posY.toFloat(), cellSize.toFloat())
                            val dst = IntOffset(posX, posY)
                            val dstSize = IntSize(cellSize, cellSize)
                            clipPath(path) {
                                drawImage(
                                    image = meadowBitmap,
                                    dstOffset = dst,
                                    dstSize = dstSize
                                )
                                for (side in shoreBitmaps.indices) {
                                    if (neighborIsWater(i, j, side)) {
                                        drawImage(
                                            image = shoreBitmaps[side],
                                            dstOffset = dst,
                                            dstSize = dstSize
                                        )
                                    }
                                }
                            }
                            val owner = cell.player
                            if (owner != null && owner.color in ownerColors.indices) {
                                val color = ownerColors[owner.color]
                                drawPath(path, color.copy(alpha = 0.22f))
                                drawPath(path, color, style = Stroke(width = cellSize * 0.06f))
                            } else {
                                drawPath(
                                    path,
                                    Color(0xFF3B2A1A),
                                    style = Stroke(width = cellSize * 0.03f)
                                )
                            }

                            /*
                            // отображение защиты клеток
                            drawIntoCanvas { canvas ->
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLUE
                                    textSize = dx * 0.4f // Размер текста адаптивный
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                val textX = posX + dx / 2f
                                val textY = posY + dx / 1.7f

                                canvas.nativeCanvas.drawText("${mass[i][j].protection}", textX, textY, textPaint)
                            }*/

                            /*
                            // Рисуем текст с индексами i и j
                            drawIntoCanvas { canvas ->
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLUE
                                    textSize = dx * 0.4f // Размер текста адаптивный
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                val textX = posX + dx / 2f
                                val textY = posY + dx / 1.7f

                                canvas.nativeCanvas.drawText("$i,$j", textX, textY, textPaint)
                            }*/

                        }

                }
            }
        }
    }

    @Composable
    fun drawRectPole(){
        for (i in mass.indices) {
            for (j in mass[i].indices) {
                if (mass[i][j].player!!.color != -1) {
                    var l = 0f
                    if (i % 2 != 0) {
                        l = (dx * 0.5).toFloat()
                    }
                    val posX: Float = (poleX + i * (dx * 0.7f))
                    val posY: Float = (poleY + j * dx + l)

                    if (posX + dx > 0 && posX < display.x &&
                        posY + dx > 0 && posY < display.y
                    ) {

                        rect(
                            modifier = Modifier,posX,posY,
                            dx.toFloat(),dx.toFloat(),5f, Color.Red
                        )
                    }
                }
            }
        }
    }
}

data class TouchState(
    val do_deltaX1: Int = 0,
    val do_deltaY1: Int = 0,
    val do_deltaX2: Int = 0,
    val do_deltaY2: Int = 0,
    val scale: String = "",
    val dist: Int = 0,
    val newdX: Int = 0
)