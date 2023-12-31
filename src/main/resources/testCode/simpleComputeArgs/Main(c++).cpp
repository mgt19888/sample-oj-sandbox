#include <iostream>
#include <cstdlib>

using namespace std;

int main(int argc, char *argv[]) {
    int a = atoi(argv[1]);
    int b = atoi(argv[2]);
    int result = a + b;
    cout << result << endl;
    return 0;
}