# Data Compression by Different Algorithms

A Java-based educational project implementing multiple lossless data compression algorithms. This repository demonstrates fundamental compression techniques including Huffman Coding, LZW (Lempel-Ziv-Welch), Run-Length Encoding (RLE), and dictionary-based methods, with comparative analysis of compression ratios and performance.

## Overview

This project provides clean, modular implementations of classic compression algorithms in Java. Each algorithm is implemented as a separate module with encoding and decoding functionality, enabling direct comparison of efficiency, compression ratio, and use-case suitability. Designed for educational purposes, the code emphasizes clarity, correctness, and adherence to algorithmic principles.

## Implemented Algorithms

### Huffman Coding
- Frequency analysis and priority queue-based tree construction
- Generation of optimal prefix-free variable-length codes
- Bit-level encoding and decoding with padding handling
- Suitable for text and symbolic data with skewed frequency distributions

### LZW (Lempel-Ziv-Welch)
- Dictionary-based compression with dynamic code table expansion
- Adaptive learning of repeated patterns without prior frequency analysis
- Efficient for repetitive data streams and structured text
- Encoder and decoder with configurable initial dictionary size

### Run-Length Encoding (RLE)
- Simple lossless compression for data with consecutive repeated values
- Effective for binary images, simple graphics, and run-heavy datasets
- Minimal overhead with straightforward implementation

### Additional Techniques (if implemented)
- Arithmetic Coding (optional extension)
- Dictionary compression variants
- Hybrid approaches combining multiple methods

## Features

- Modular architecture with separate classes for each algorithm
- Unified interface for encoding and decoding operations
- File I/O support for compressing and decompressing arbitrary files
- Compression ratio calculation and performance timing utilities
- Bit manipulation utilities for efficient bit-level encoding
- Error handling for invalid input and corrupted compressed data
- Console-based demonstration driver with menu selection

## Technologies Used

- Java 11 or higher
- Maven build system (pom.xml configuration)
- Standard Java I/O and Collections frameworks
- Bit manipulation via BitSet or custom bit stream handlers
- Object-Oriented Design patterns for extensibility

## Project Structure

```
Data-Compression-by-different-algorithms/
├── pom.xml # Maven build configuration
├── src/main/java/org/os/
│ ├── compression/
│ │ ├── huffman/
│ │ │ ├── HuffmanEncoder.java
│ │ │ ├── HuffmanDecoder.java
│ │ │ ├── HuffmanTree.java
│ │ │ └── FrequencyAnalyzer.java
│ │ ├── lzw/
│ │ │ ├── LZWEncoder.java
│ │ │ ├── LZWDecoder.java
│ │ │ └── Dictionary.java
│ │ ├── rle/
│ │ │ ├── RLEEncoder.java
│ │ │ └── RLEDecoder.java
│ │ ├── utils/
│ │ │ ├── BitStream.java
│ │ │ ├── CompressionStats.java
│ │ │ └── FileUtil.java
│ │ └── CompressionAlgorithm.java # Common interface
│ └── Main.java # CLI driver
├── input.png # Sample input file
├── .gitignore
└── .gitattributes
```

## Prerequisites

- Java Development Kit (JDK) 11 or later
- Maven 3.6+ for dependency management and build automation
- Git for version control and repository cloning

## Building the Project

1. Clone the repository:
```bash
git clone https://github.com/z00xINe/Data-Compression-by-different-algorithms.git
cd Data-Compression-by-different-algorithms
```
2. Build using Maven:
```
mvn clean compile
```
3. Package into executable JAR:
```
mvn package
```
4. Run the application
```
java -jar target/data-compression.jar
```

## Usage Examples

- Compress a File with Huffman Coding
```
java -jar target/data-compression.jar huffman encode input.txt output.huff
java -jar target/data-compression.jar huffman decode output.huff restored.txt
```
- Compress with LZW
```
java -jar target/data-compression.jar lzw encode input.txt output.lzw
java -jar target/data-compression.jar lzw decode output.lzw restored.txt
```
- Compare Compression Ratios
```
java -jar target/data-compression.jar compare input.txt
# Outputs: Algorithm | Original Size | Compressed Size | Ratio | Time
```

## Programmatic Usage

```
CompressionAlgorithm huffman = new HuffmanEncoder();
byte[] compressed = huffman.encode("Hello World".getBytes());
byte[] decompressed = huffman.decode(compressed);
System.out.println(new String(decompressed)); // Hello World
```

## Compression Metrics

- The project includes utilities to measure:
  - Compression ratio: (compressed size / original size) * 100
  - Encoding/decoding time in milliseconds
  - Memory usage during compression operations
  - Bit efficiency for variable-length coding schemes

## Educational Value

- This repository is designed to help learners:
  - Understand the theory behind lossless compression algorithms
  - Compare trade-offs between algorithm complexity and compression efficiency
  - Practice bit-level manipulation and file I/O in Java
  - Explore data structure applications (priority queues, hash maps, trees)

## Contributing

- Contributions are welcome. Suggested improvements:
  - Add support for additional algorithms (Arithmetic Coding, BWT)
  - Implement parallel compression for large files
  - Add GUI frontend using JavaFX or Swing
  - Include unit tests with JUnit for regression testing
  - Add benchmarking suite with diverse dataset categories

## Limitations

- Implementations prioritize clarity over maximum performance
- Huffman coding uses byte-level symbols; extensions to bit-level or multi-byte symbols possible
- LZW dictionary size is fixed; adaptive resizing not implemented by default
- RLE is most effective on highly repetitive data; not suitable for random data

## License

- This project is provided for educational purposes. Feel free to use, modify, and distribute with attribution.

## Authors
- Youssef Mohamed (@z00xINe)
