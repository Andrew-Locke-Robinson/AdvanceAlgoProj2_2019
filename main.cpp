#include "ImageConverter.hpp"
#include <iostream>



int main(int argc, char **argv)
{
  switch(argv[1][0]) {
  case '1':
    std::cout << "first option\n";
    ImageConverter::ascii_to_binary(argv[2]);
    break;
  case '2':
    std::cout << "second option\n";
    ImageConverter::binary_to_ascii(argv[2]);
    break;
  default:
    std::cout << "not a valid option\n";
    break;
  }

  return 0;
}

