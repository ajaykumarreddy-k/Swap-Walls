#!/usr/bin/env python3
"""
prepare_wallpapers.py
---------------------
Resizes wallpaper images, creates thumbnails, and updates manifest.json.
 
Usage:
    pip install Pillow colorthief
    python prepare_wallpapers.py --category nature
 
Put source images in ./input/ before running.
Processed images go to ./output/<category>/
manifest.json is updated automatically.
"""
 
import os
import sys
import json
import argparse
import shutil
from pathlib import Path
 
try:
    from PIL import Image
    from colorthief import ColorThief
except ImportError:
    print("Run first: pip install Pillow colorthief")
    sys.exit(1)
 
# Settings
FULL_MAX_WIDTH  = 1080
FULL_MAX_HEIGHT = 1920
THUMB_WIDTH     = 400
THUMB_HEIGHT    = 700
QUALITY         = 88  # JPEG quality (1-95)
 
 
def resize_image(img: Image.Image, max_w: int, max_h: int) -> Image.Image:
    img.thumbnail((max_w, max_h), Image.LANCZOS)
    return img
 
 
def get_dominant_color(filepath: str) -> str:
    try:
        ct = ColorThief(filepath)
        r, g, b = ct.get_color(quality=1)
        return "#{:02X}{:02X}{:02X}".format(r, g, b)
    except Exception:
        return "#1565C0"
 
 
def next_id(existing_ids: list, category: str) -> str:
    """Generate next ID like nature_042"""
    nums = []
    prefix = category + "_"
    for eid in existing_ids:
        if eid.startswith(prefix):
            try:
                nums.append(int(eid[len(prefix):]))
            except ValueError:
                pass
    next_num = max(nums) + 1 if nums else 1
    return f"{category}_{next_num:03d}"
 
 
def process_images(category: str):
    input_dir  = Path("input")
    output_dir = Path("output") / category
    output_dir.mkdir(parents=True, exist_ok=True)
 
    manifest_path = Path("manifest.json")
    if manifest_path.exists():
        with open(manifest_path) as f:
            manifest = json.load(f)
    else:
        manifest = {"version": 1, "updated": "", "categories": []}
 
    # Find or create the category in manifest
    cat_entry = next((c for c in manifest["categories"] if c["id"] == category), None)
    if cat_entry is None:
        cat_entry = {"id": category, "name": category.capitalize(), "icon": "image", "wallpapers": []}
        manifest["categories"].append(cat_entry)
 
    existing_files = {w["file"] for w in cat_entry["wallpapers"]}
    existing_ids   = [w["id"] for w in cat_entry["wallpapers"]]
 
    supported = {".jpg", ".jpeg", ".png", ".webp"}
    added = 0
 
    for src_path in sorted(input_dir.iterdir()):
        if src_path.suffix.lower() not in supported:
            continue
        if src_path.name in existing_files:
            print(f"  Skip (already in manifest): {src_path.name}")
            continue
 
        print(f"  Processing: {src_path.name}")
        stem = src_path.stem.lower().replace(" ", "_")
        out_file  = f"{stem}.jpg"
        out_thumb = f"{stem}_thumb.jpg"
 
        # Full size
        img_full = Image.open(src_path).convert("RGB")
        img_full = resize_image(img_full, FULL_MAX_WIDTH, FULL_MAX_HEIGHT)
        full_path = output_dir / out_file
        img_full.save(full_path, "JPEG", quality=QUALITY)
 
        # Thumbnail
        img_thumb = Image.open(src_path).convert("RGB")
        img_thumb = resize_image(img_thumb, THUMB_WIDTH, THUMB_HEIGHT)
        thumb_path = output_dir / out_thumb
        img_thumb.save(thumb_path, "JPEG", quality=QUALITY)
 
        # Dominant color
        color = get_dominant_color(str(full_path))
 
        # New manifest entry
        new_id = next_id(existing_ids, category)
        existing_ids.append(new_id)
        cat_entry["wallpapers"].append({
            "id":    new_id,
            "title": stem.replace("_", " ").title(),
            "file":  out_file,
            "thumb": out_thumb,
            "color": color
        })
        added += 1
 
    # Update manifest date
    from datetime import date
    manifest["updated"] = str(date.today())
 
    with open(manifest_path, "w") as f:
        json.dump(manifest, f, indent=2)
 
    print(f"\nDone. Added {added} wallpapers to category '{category}'.")
    print(f"Files are in: {output_dir}")
    print(f"manifest.json updated.")
 
 
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--category", required=True, help="Category slug, e.g. 'nature'")
    args = parser.parse_args()
    process_images(args.category.lower())
