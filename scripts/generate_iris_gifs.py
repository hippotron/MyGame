"""Generate iris close/open GIFs with a transparent circular hole."""

from math import hypot
from pathlib import Path

from PIL import Image, ImageDraw

W, H = 540, 960
CX, CY = W // 2, H // 2
MAX_R = int(hypot(CX, CY)) + 2
FRAME_COUNT = 18
FRAME_DURATION_MS = 70  # 18 * 70 = 1260ms per phase

ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "app" / "src" / "main" / "res" / "raw"


def ease(t: float) -> float:
    return t * t * (3.0 - 2.0 * t)


def make_frame(radius: float) -> Image.Image:
    img = Image.new("RGBA", (W, H), (0, 0, 0, 255))
    if radius > 0:
        draw = ImageDraw.Draw(img)
        r = int(round(radius))
        draw.ellipse((CX - r, CY - r, CX + r, CY + r), fill=(0, 0, 0, 0))
    return img


def to_p_mode(rgba: Image.Image) -> Image.Image:
    """Index 0 = black, index 1 = transparent."""
    alpha = rgba.getchannel("A")
    palette_img = Image.new("P", rgba.size)
    palette = [0, 0, 0, 0, 0, 0] + [0] * (254 * 3)
    palette_img.putpalette(palette)
    mask = alpha.point(lambda a: 0 if a > 127 else 1)
    palette_img.putdata(list(mask.get_flattened_data()))
    palette_img.info["transparency"] = 1
    palette_img.info["disposal"] = 2
    return palette_img


def radii_close():
    for i in range(FRAME_COUNT):
        t = i / (FRAME_COUNT - 1)
        yield MAX_R * (1.0 - ease(t))


def radii_open():
    for i in range(FRAME_COUNT):
        t = i / (FRAME_COUNT - 1)
        yield MAX_R * ease(t)


def save_gif(path: Path, radii) -> None:
    frames = [to_p_mode(make_frame(r)) for r in radii]
    frames[0].save(
        path,
        save_all=True,
        append_images=frames[1:],
        duration=FRAME_DURATION_MS,
        loop=1,
        disposal=2,
        transparency=1,
    )
    print(f"Wrote {path} ({len(frames)} frames, {FRAME_DURATION_MS}ms each)")


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    save_gif(OUT_DIR / "iris_close.gif", radii_close())
    save_gif(OUT_DIR / "iris_open.gif", radii_open())


if __name__ == "__main__":
    main()
