# Matrix-Multiplier

This application multiplies two matricies in the form:
---- (M x N) * (N x P) ----
Where Matrix 1 has M rows and N columns, and Matrix 2 having N rows and P columns.

It does so by using a bounded buffer and multi-threading.
The producers will split up the matricies, and put pieces onto the buffer.
The consumers will remove items from the buffer and do multiplication, then construct final matrix

## Instructions

The application will launch upon start. Within the "config.dat" file, you will see the initial configurations as well as some given suggesstions.
You can alter the variables to see different results.
Import this project into your favorite IDE and run the "driver" file.
