# Matrix Multiplier

## Description

This Java application multiplies two matrices in the form:

(M x N) * (N x P)

Where Matrix A has M rows and N columns, and Matrix B has N rows and P columns.

The multiplication is performed using a bounded buffer and multi-threading approach. Producers split up the matrices and put pieces onto the buffer, while consumers remove items from the buffer, perform multiplication, and construct the final matrix.

## Key Components

- `Buffer`: Implements the bounded buffer for storing work items.
- `Consumer`: Consumes work items from the buffer and performs matrix multiplication.
- `Producer`: Splits matrices and produces work items for the buffer.
- `WorkItem`: Represents a unit of work containing submatrices for multiplication.
- `Driver`: Main class that initializes and manages the matrix multiplication process.

## Features

- Configurable matrix dimensions (M, N, P)
- Adjustable number of producer and consumer threads
- Customizable buffer size
- Random matrix generation
- Performance statistics (simulation time, sleep times, buffer full/empty counts)

## How to Use

1. Clone the repository or download the source code.
2. Import the project into your preferred Java IDE.
3. Modify the `config.dat` file to adjust parameters as needed:
   - Matrix dimensions (M, N, P)
   - Number of producer and consumer threads
   - Maximum buffer size
   - Split size for work items
   - Maximum sleep times for producers and consumers
4. Run the `Driver` class to start the matrix multiplication process.

## Configuration

Edit the `config.dat` file to customize the following parameters:

```
M <value>
N <value>
P <value>
NumProducer <value>
NumConsumer <value>
MaxBuffSize <value>
SplitSize <value>
MaxProducerSleepTime <value>
MaxConsumerSleepTime <value>
```

## Output

The program will display:
- Input matrices A and B
- Resulting matrix C (from bounded buffer approach)
- Verification matrix C2 (from standard matrix multiplication)
- Performance statistics

## Requirements

- Java Development Kit (JDK) 8 or higher

## Future Improvements

- Implement error handling for invalid configurations
- Add support for reading input matrices from files
- Optimize memory usage for large matrices
- Implement a more sophisticated work distribution algorithm

## Contributing

Contributions to improve the project are welcome. Please follow these steps:

1. Fork the repository
2. Create a new branch
3. Make your changes and commit them
4. Push to your fork and submit a pull request

## License

This project is open-source and available under the [MIT License](LICENSE).

