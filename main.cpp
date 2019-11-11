#include "ImageConverter.hpp"
#include <iostream>



int main(int argc, char **argv)
{
  switch(argv[1][0]) {
  case '1':
    ImageConverter::ascii_to_binary(argv[2]);
    break;
  case '2':
    ImageConverter::binary_to_ascii(argv[2]);
    break;
  case '3':
    ImageConverter::SVD_to_compressed(argv[2],argv[3],argv[4]);
    break;
    case '4':
    ImageConverter::compressed_to_pgm(argv[2]);
    break;
  default:
    std::cout << "not a valid option\n";
    break;
  }

  return 0;
}

