#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    int a = atoi(argv[1]);
    int b = atoi(argv[2]);
    int result = a + b;
    printf("%d", result);
    return 0;
}