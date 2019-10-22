#include "ImageConverter.hpp"
#include <fstream>
#include <iostream>
#include <string>

void ImageConverter::ascii_to_binary(char *filename)
{
  std::cout << "You asked to convert file " << filename
            << " to binary\n";
  std::ifstream in_file(filename);
  std::string input;

  if(in_file.is_open()) {
    while(getline(in_file, input)) {
      std::cout << input << std::endl;
    }
  }
}

void ImageConverter::binary_to_ascii(char *filename)
{
  std::cout << "You asked to convert file " << filename
            << " to ascii\n";
}