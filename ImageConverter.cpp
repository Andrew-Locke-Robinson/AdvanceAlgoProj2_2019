#include "ImageConverter.hpp"
#include <fstream>
#include <iostream>
#include <string>

void ImageConverter::ascii_to_binary(char *filename)
{
  std::string in_file_name(filename);
  in_file_name += ".pgm";

  std::string out_file_name(filename);
  out_file_name += "_b.bin";

  std::ifstream in_file(in_file_name, std::ios::binary);
  std::ofstream out_file(out_file_name, std::ios::binary);
  std::string input;

  if(in_file.is_open()) {
    getline(in_file, input); // remove P2
    char check_for_comment = in_file.peek();
    if(check_for_comment == '#') {
      getline(in_file, input); // remove the comment line
    }
    short width, height;
    width = 0;
    height = 0;

    in_file >> width >> height; // next two numbers are two bytes
    char ch[2];
    ch[0] = (unsigned char)(width >> 8);
    ch[1] = (unsigned char)width;
    short num = (unsigned char)ch[0];
    num = num << 8;
    num += (unsigned char)ch[1];
    out_file.write(ch, 2);
    ch[0] = (char)(height >> 8);
    ch[1] = (char)height;
    out_file.write(ch, 2);

    int nums;
    while(in_file >> nums) {
      ch[0] = static_cast<unsigned char>(nums);
      out_file.write(ch, 1);
    }
  }
  else {
    std::cout << "Enter the filename without the \".pgm\" extension\n";
  }
}

void ImageConverter::binary_to_ascii(char *filename)
{
  std::string in_file_name(filename);
  in_file_name += ".bin";
  std::string out_file_name(filename);
  out_file_name.erase(out_file_name.size() - 2, 2); // this erases the "_b"
  out_file_name += "_copy.pgm";

  std::ifstream in_file(in_file_name, std::ios::binary);
  std::ofstream out_file(out_file_name, std::ios::binary);

  if(in_file.is_open()) {
    short width, height;
    width = 0;
    height = 0;

    in_file.seekg(0, in_file.end);
    long length = in_file.tellg();
    in_file.seekg(0, in_file.beg);

    char *buffer = new char [length];
    in_file.read(buffer, length);

    char ch[2];
    ch[0] = buffer[0];
    ch[1] = buffer[1];
    width = (unsigned char)buffer[0];
    width = width << 8;
    width += (unsigned char)buffer[1];
    ch[0] = (unsigned char)buffer[2];
    ch[1] = (unsigned char)buffer[3];
    height = (unsigned char)buffer[2];
    height = height << 8;
    height += (unsigned char)buffer[3];

    out_file << "P2\n"; // simple pgm format
    out_file << width << " " << height << std::endl;
    out_file << (int)(unsigned char)buffer[4] << std::endl;

    for(int i = 5; i < length; ++i) {
      out_file << (int)(unsigned char)buffer[i] << " ";
      if((i - 5) % 10 == 0) {
        out_file << std::endl;
      }
    }
    delete[] buffer;
  }
  else {
    std::cout << "Enter the filename without the \".bin\" extension\n";
  }
}

void ImageConverter::SVD_to_compressed(char* headerfile_, char* SVD_file_, char* k_value_)
{
  int k_value = std::stoi(std::string(k_value_));
  std::ifstream header_file(headerfile_);
  std::ifstream SVD_file(SVD_file_);

  std::cout << "The k value you selected was " << k_value << std::endl;

  if(header_file.is_open())
  {
    std::cout << "header file is open\n";
  }

  if(SVD_file.is_open())
  {
    std::cout << "svd file is open\n";
  }
}

void ImageConverter::compressed_to_pgm(char* compressed_file)
{

}