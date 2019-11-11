#include "ImageConverter.hpp"
#include "half.hpp"
#include <vector>
#include <fstream>
#include <iostream>
#include <string>
#include <limits>

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

    width = (unsigned char)buffer[0];
    width = width << 8;
    width += (unsigned char)buffer[1];
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

/*
* The header file contains width, height, and gray scale in that order
* the SVD file contains three matrices, one row at a time {U,S,V} in that order,
* U and V are square matrices with sizes NxN where N is the height or
* width respectively. The k value tells you how many values you should retain
* from the middle, sigma, matrix. The k value also is the amount of columns
* you will store from U and the amount of rows you will store from V.
*/
void ImageConverter::SVD_to_compressed(char *headerfile_name, char *SVD_file_name, char *k_value_raw)
{
  using half_float::half;

  uint16_t k_value = std::stoi(std::string(k_value_raw));
  std::ifstream header_file(headerfile_name);
  std::ifstream SVD_file(SVD_file_name);

  std::vector<half> U_values;
  std::vector<half> S_values;
  std::vector<half> V_values;

  // process header file first
  uint16_t width, height; // 2 bytes for each
  uint8_t greyscale; // 1 byte always

  header_file >> width >> height >> greyscale;

  // process U matrix
  for(int i = 0; i < height; ++i) {
    // process k columns in that row
    for(int j = 0; j < k_value; ++j) {
      float element_;
      SVD_file >> element_;
      half new_u_value_(element_);
      U_values.push_back(new_u_value_);
    }
    // go to the next row
    SVD_file.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
  }

  // process S matrix
  for(int i = 0; i < height; ++i) {
    // find the s value in row i
    for(int j = 0; j < k_value; ++j) {
      float temp_;
      SVD_file >> temp_;

      if(i == j) { // these are the diagnol values
        half real_s_value(temp_);
        S_values.push_back(real_s_value);
        break;// no more s values in this row
      }
    }
    // go to the next row
    SVD_file.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
  }

  // process V matrix
  for(int i = 0; i < k_value; ++i) {
    // we need every value in each row, but only k rows
    for(int j = 0; j < width; ++j) {
      float element_;
      SVD_file >> element_;
      half new_u_value_(element_);
      U_values.push_back(new_u_value_);
    }
    // go to the next row
    SVD_file.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
  }

  /*
  * at this point we need to save width, height, greyscale, k value and the {U,S,V}
  * values to a binary format, we're going to save the information in the
  * same order that we created it. This ensures that we can extract it later
  * and turn it back to a pgm image.
  */

  std::ofstream out_file("image_b.pgm.SVD");
  char data[2];

  // write to the output width, height, greyscale, and k value in that order
  data[0] = (unsigned char)(width >> 8);
  data[1] = (unsigned char)width;
  out_file.write(data, 2);
  data[0] = (unsigned char)(height >> 8);
  data[1] = (unsigned char)height;
  out_file.write(data, 2);
  data[0] = (unsigned char)greyscale;
  out_file.write(data, 1);
  data[0] = (unsigned char)(k_value >> 8);
  data[1] = (unsigned char)k_value;
  out_file.write(data, 2);

  for(int i = 0; i < U_values.size(); ++i) {
    data[0] = (unsigned char)((short)(U_values[i]) >> 8);
    data[1] = (unsigned char)(U_values[i]);
    out_file.write(data, 2);
  }

  for(int i = 0; i < S_values.size(); ++i) {
    data[0] = (unsigned char)((short)(S_values[i]) >> 8);
    data[1] = (unsigned char)(S_values[i]);
    out_file.write(data, 2);
  }

  for(int i = 0; i < V_values.size(); ++i) {
    data[0] = (unsigned char)((short)(V_values[i]) >> 8);
    data[1] = (unsigned char)(V_values[i]);
    out_file.write(data, 2);
  }

}

void ImageConverter::compressed_to_pgm(char *compressed_file_name)
{
  std::ifstream in_file(compressed_file_name);

  if(in_file.is_open()) {
    uint16_t width, height, k_value;
    uint8_t greyscale = 0;
    width = 0;
    height = 0;
    k_value = 0;

    in_file.seekg(0, in_file.end);
    long length = in_file.tellg();
    in_file.seekg(0, in_file.beg);

    char *buffer = new char [length];
    in_file.read(buffer, length);

    width = (unsigned char)buffer[0];
    width = width << 8; // shift to the left
    width += (unsigned char)buffer[1];
    height = (unsigned char)buffer[2];
    height = height << 8;
    height += (unsigned char)buffer[3];
    greyscale = buffer[4];
    k_value = (unsigned char)buffer[4];
    k_value = k_value << 8;
    k_value += (unsigned char)buffer[5];


    std::ofstream out_file("image_" + std::to_string(k_value) + ".pgm");

    out_file << "P2\n"; // simple pgm format
    out_file << (int)width << " " << (int)height << std::endl;
    out_file << (int)greyscale << std::endl;

    for(int i = 5; i < length; ++i) {
      out_file << (int)(unsigned char)buffer[i] << " ";
      if((i - 5) % 10 == 0) {
        out_file << std::endl;
      }
    }
    delete[] buffer;
  }
}