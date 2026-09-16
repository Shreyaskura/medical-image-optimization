# Medical Image Optimization Using Matrix Chain Multiplication

## Project Overview

This project applies the Matrix Chain Multiplication (MCM) dynamic
programming technique to a medical image processing pipeline.

The dataset contains 100 public medical image records:

- 50 MRI images
- 50 X-Ray images

The images are converted into grayscale matrices and resized to
32 × 32 for matrix-based processing.

## Methodology

Medical Images
        ↓
Image Preprocessing
        ↓
Grayscale Conversion
        ↓
32 × 32 Matrix
        ↓
Image Processing Matrix Chain
        ↓
Matrix Chain Multiplication
        ↓
Dynamic Programming
        ↓
Minimum Scalar Multiplications

## Matrix Chain

The image-processing pipeline uses the following compatible matrices:

A1 = 32 × 32
A2 = 32 × 16
A3 = 16 × 8
A4 = 8 × 4

Matrix Chain:

A1 × A2 × A3 × A4

## Optimization Result

Using Matrix Chain Multiplication:

- Conventional left-to-right cost: 21,504 scalar multiplications
- Optimized MCM cost: 6,656 scalar multiplications
- Scalar multiplications saved: 14,848

The MCM algorithm uses dynamic programming to determine an efficient
parenthesization of the matrix chain.

## Algorithms

### Matrix Chain Multiplication

The recurrence used is:

dp[i][j] = min(
    dp[i][k] + dp[k+1][j]
    + p[i] × p[k+1] × p[j+1]
)

where k represents the possible split position.

### Complexity

Time Complexity: O(n³)

Space Complexity: O(n²)

## Dataset

The project uses publicly available, de-identified medical images.
No personally identifying patient information is included in this
project.

## Files

- `MRI/` - 50 MRI images
- `XRay/` - 50 X-Ray images
- `generate_excel.py` - Converts images into matrices and creates Excel data
- `mcm_optimizer.py` - Performs MCM optimization
- `Medical_Image_Matrices.xlsx` - Image matrix data
- `Medical_Image_Matrices_MCM.xlsx` - MCM optimization results
- `README.md` - Project documentation

## Team

DSA Project

## Note

The 32 × 32 image matrix is a resized representation used for
matrix-based processing. The additional matrices in the processing
chain represent stages of the image-processing pipeline and are not
claimed to be separate patient scans.