#pragma once
#include <iostream>


class ImageConverter
{
	public:

		static void ascii_to_binary(char* filename);

		static void binary_to_ascii(char* filename);

		static void SVD_to_compressed(char* headerfile, char* SVD_file, char* k_value);

		static void compressed_to_pgm(char* compressed_file);
};