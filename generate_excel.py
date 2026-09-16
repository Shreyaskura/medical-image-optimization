from PIL import Image
from pathlib import Path
from openpyxl import Workbook

# Project folders
MRI_FOLDER = Path("MRI")
XRAY_FOLDER = Path("XRay")

# Output Excel file
OUTPUT_FILE = "Medical_Image_Matrices.xlsx"

# Matrix size for Excel
MATRIX_SIZE = 32


def get_images(folder):
    extensions = {".jpg", ".jpeg", ".png", ".bmp"}
    return sorted(
        [f for f in folder.iterdir() if f.suffix.lower() in extensions]
    )


def add_image_to_excel(ws, patient_id, image_type, image_path):
    # Open image
    image = Image.open(image_path)

    # Original dimensions
    original_width, original_height = image.size

    # Convert to grayscale
    gray = image.convert("L")

    # Resize to 32 x 32 for a manageable matrix
    matrix_image = gray.resize((MATRIX_SIZE, MATRIX_SIZE))

    # Header information
    ws.append([
        patient_id,
        image_type,
        image_path.name,
        original_height,
        original_width,
        f"{original_height} x {original_width}"
    ])

    # Matrix header
    ws.append(["32 x 32 Grayscale Matrix"])

    # Write matrix values
    pixels = matrix_image.load()

    for row in range(MATRIX_SIZE):
        values = [pixels[col, row] for col in range(MATRIX_SIZE)]
        ws.append(values)

    # Empty row between images
    ws.append([])


# Create workbook
wb = Workbook()

# Remove default sheet
default_sheet = wb.active
wb.remove(default_sheet)

# Create summary sheet
summary = wb.create_sheet("Summary")

summary.append([
    "Patient ID",
    "Image Type",
    "Image Name",
    "Original Rows",
    "Original Columns",
    "Original Matrix Size"
])

patient_number = 1

# Process MRI images
for image_path in get_images(MRI_FOLDER)[:50]:
    add_image_to_excel(
        summary,
        f"P{patient_number:03d}",
        "MRI",
        image_path
    )
    patient_number += 1

# Process X-ray images
for image_path in get_images(XRAY_FOLDER)[:50]:
    add_image_to_excel(
        summary,
        f"P{patient_number:03d}",
        "X-Ray",
        image_path
    )
    patient_number += 1

# Create separate sheets for actual matrices
matrix_sheet = wb.create_sheet("Matrices")

patient_number = 1

for image_type, folder in [("MRI", MRI_FOLDER), ("X-Ray", XRAY_FOLDER)]:

    for image_path in get_images(folder)[:50]:

        image = Image.open(image_path).convert("L")
        matrix_image = image.resize((MATRIX_SIZE, MATRIX_SIZE))
        pixels = matrix_image.load()

        matrix_sheet.append([
            f"P{patient_number:03d}",
            image_type,
            image_path.name,
            "32 x 32"
        ])

        for row in range(MATRIX_SIZE):
            matrix_sheet.append(
                [pixels[col, row] for col in range(MATRIX_SIZE)]
            )

        matrix_sheet.append([])

        patient_number += 1

# Save Excel
wb.save(OUTPUT_FILE)

print()
print("====================================")
print("Excel generation completed!")
print("====================================")
print(f"Total images processed: {patient_number - 1}")
print(f"Excel file: {OUTPUT_FILE}")
print("====================================")