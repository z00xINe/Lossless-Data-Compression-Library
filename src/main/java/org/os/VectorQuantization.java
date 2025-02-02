package org.os;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VectorQuantization {

    // Compression Method
    public static void compress(String inputImagePath, String outputFilePath, int blockSize, int codebookSize) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        int width = image.getWidth();
        int height = image.getHeight();

        // Convert image to grayscale matrix
        int[][] grayscaleMatrix = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff; // Extract red channel (grayscale image)
                grayscaleMatrix[y][x] = gray;
            }
        }

        // Split into blocks
        List<int[][]> blocks = splitIntoBlocks(grayscaleMatrix, blockSize);

        // Generate codebook
        List<int[][]> codebook = generateCodebook(blocks, codebookSize);

        // Quantize blocks
        int[] quantizedBlocks = quantizeBlocks(blocks, codebook);

        // Save compressed data
        saveCompressedFile(outputFilePath, codebook, quantizedBlocks, width, height, blockSize);
    }

    // Decompression Method
    public static void decompress(String inputFilePath, String outputImagePath) throws IOException {
        // Load compressed data
        CompressedData data = loadCompressedFile(inputFilePath);

        // Reconstruct image
        int[][] decompressedMatrix = reconstructImage(data);

        // Save decompressed image
        BufferedImage outputImage = new BufferedImage(data.width, data.height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                int gray = decompressedMatrix[y][x];
                int rgb = (gray << 16) | (gray << 8) | gray;
                outputImage.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(outputImage, "png", new File(outputImagePath));
    }

    // Helper Methods

    private static List<int[][]> splitIntoBlocks(int[][] matrix, int blockSize) {
        List<int[][]> blocks = new ArrayList<>();
        for (int i = 0; i < matrix.length; i += blockSize) {
            for (int j = 0; j < matrix[0].length; j += blockSize) {
                int[][] block = new int[blockSize][blockSize];
                for (int bi = 0; bi < blockSize && i + bi < matrix.length; bi++) {
                    for (int bj = 0; bj < blockSize && j + bj < matrix[0].length; bj++) {
                        block[bi][bj] = matrix[i + bi][j + bj];
                    }
                }
                blocks.add(block);
            }
        }
        return blocks;
    }

    private static List<int[][]> generateCodebook(List<int[][]> blocks, int codebookSize) {
        List<int[][]> codebook = new ArrayList<>();

        // Step 1: Initialize with the average vector of all blocks
        int blockSize = blocks.get(0).length;
        int[][] initialCentroid = new int[blockSize][blockSize];

        // Calculate the average block (centroid)
        for (int[][] block : blocks) {
            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    initialCentroid[i][j] += block[i][j];
                }
            }
        }
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                initialCentroid[i][j] /= blocks.size();
            }
        }
        codebook.add(initialCentroid);

        // Step 2: Split and refine centroids until the desired codebook size is reached
        while (codebook.size() < codebookSize) {
            List<int[][]> newCentroids = new ArrayList<>();
            for (int[][] centroid : codebook) {
                // Create two new centroids by adding/subtracting a small value (delta)
                int[][] centroid1 = new int[blockSize][blockSize];
                int[][] centroid2 = new int[blockSize][blockSize];
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        centroid1[i][j] = centroid[i][j] - 1;
                        centroid2[i][j] = centroid[i][j] + 1;
                    }
                }
                newCentroids.add(centroid1);
                newCentroids.add(centroid2);
            }
            codebook = newCentroids;

            // Re-cluster blocks to nearest centroids
            boolean convergence = false;
            while (!convergence) {
                // Assign blocks to the closest centroids
                List<List<int[][]>> clusters = new ArrayList<>();
                for (int i = 0; i < codebook.size(); i++) {
                    clusters.add(new ArrayList<>());
                }
                for (int[][] block : blocks) {
                    int closestIndex = findClosestCodebookIndex(block, codebook);
                    clusters.get(closestIndex).add(block);
                }

                // Update centroids based on clusters
                convergence = true;
                for (int i = 0; i < codebook.size(); i++) {
                    int[][] newCentroid = calculateCentroid(clusters.get(i), blockSize);
                    if (!areBlocksEqual(codebook.get(i), newCentroid)) {
                        convergence = false;
                        codebook.set(i, newCentroid);
                    }
                }
            }
        }
        return codebook;
    }

    private static int[][] calculateCentroid(List<int[][]> cluster, int blockSize) {
        int[][] centroid = new int[blockSize][blockSize];
        if (cluster.isEmpty()) return centroid;

        for (int[][] block : cluster) {
            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    centroid[i][j] += block[i][j];
                }
            }
        }
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                centroid[i][j] /= cluster.size();
            }
        }
        return centroid;
    }

    private static boolean areBlocksEqual(int[][] block1, int[][] block2) {
        for (int i = 0; i < block1.length; i++) {
            for (int j = 0; j < block1[0].length; j++) {
                if (block1[i][j] != block2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] quantizeBlocks(List<int[][]> blocks, List<int[][]> codebook) {
        int[] quantizedBlocks = new int[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            quantizedBlocks[i] = findClosestCodebookIndex(blocks.get(i), codebook);
        }
        return quantizedBlocks;
    }

    private static int findClosestCodebookIndex(int[][] block, List<int[][]> codebook) {
        int minIndex = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < codebook.size(); i++) {
            double distance = calculateBlockDistance(block, codebook.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return minIndex;
    }

    private static double calculateBlockDistance(int[][] block1, int[][] block2) {
        double distance = 0;
        for (int i = 0; i < block1.length; i++) {
            for (int j = 0; j < block1[0].length; j++) {
                distance += Math.pow(block1[i][j] - block2[i][j], 2);
            }
        }
        return Math.sqrt(distance);
    }

    private static void saveCompressedFile(String filePath, List<int[][]> codebook, int[] quantizedBlocks, int width, int height, int blockSize) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Save codebook size
            writer.write(codebook.size() + "\n");

            // Save codebook
            for (int[][] block : codebook) {
                for (int[] row : block) {
                    for (int value : row) {
                        writer.write(value + " ");
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }

            // Save quantized blocks
            for (int value : quantizedBlocks) {
                writer.write(value + " ");
            }
            writer.write("\n");

            // Save metadata
            writer.write(width + "\n");
            writer.write(height + "\n");
            writer.write(blockSize + "\n");
        }
    }

    private static CompressedData loadCompressedFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Load codebook size
            int codebookSize = Integer.parseInt(reader.readLine());
            List<int[][]> codebook = new ArrayList<>();

            // Load codebook
            for (int i = 0; i < codebookSize; i++) {
                List<int[]> blockRows = new ArrayList<>();
                String line;
                while (!(line = reader.readLine()).isEmpty()) {
                    String[] values = line.split(" ");
                    int[] row = new int[values.length];
                    for (int j = 0; j < values.length; j++) {
                        row[j] = Integer.parseInt(values[j]);
                    }
                    blockRows.add(row);
                }
                codebook.add(blockRows.toArray(new int[blockRows.size()][]));
            }

            // Load quantized blocks
            String[] quantizedValues = reader.readLine().split(" ");
            int[] quantizedBlocks = new int[quantizedValues.length];
            for (int i = 0; i < quantizedValues.length; i++) {
                quantizedBlocks[i] = Integer.parseInt(quantizedValues[i]);
            }

            // Load metadata
            int width = Integer.parseInt(reader.readLine());
            int height = Integer.parseInt(reader.readLine());
            int blockSize = Integer.parseInt(reader.readLine());

            return new CompressedData(codebook, quantizedBlocks, width, height, blockSize);
        }
    }

    private static int[][] reconstructImage(CompressedData data) {
        int[][] matrix = new int[data.height][data.width];
        int index = 0;
        for (int i = 0; i < data.height; i += data.blockSize) {
            for (int j = 0; j < data.width; j += data.blockSize) {
                int[][] block = data.codebook.get(data.quantizedBlocks[index++]);
                for (int bi = 0; bi < data.blockSize && i + bi < data.height; bi++) {
                    for (int bj = 0; bj < data.blockSize && j + bj < data.width; bj++) {
                        matrix[i + bi][j + bj] = block[bi][bj];
                    }
                }
            }
        }
        return matrix;
    }

    // Data class for compressed data
    private static class CompressedData {
        List<int[][]> codebook;
        int[] quantizedBlocks;
        int width, height, blockSize;

        public CompressedData(List<int[][]> codebook, int[] quantizedBlocks, int width, int height, int blockSize) {
            this.codebook = codebook;
            this.quantizedBlocks = quantizedBlocks;
            this.width = width;
            this.height = height;
            this.blockSize = blockSize;
        }
    }

    // Main method for testing
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Block size: ");
        int blockSize = sc.nextInt();

        System.out.println("codebook size: ");
        int codebookSize = sc.nextInt();

        compress("input.png", "compressed.txt", blockSize, codebookSize);

        decompress("compressed.txt", "output.png");
    }
}