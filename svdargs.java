import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class svdargs {

	public svdargs() {
		// TODO Auto-generated constructor stub
	}

	public static float toFloat( int hbits )
	{
	    int mant = hbits & 0x03ff;            // 10 bits mantissa
	    int exp =  hbits & 0x7c00;            // 5 bits exponent
	    if( exp == 0x7c00 )                   // NaN/Inf
	        exp = 0x3fc00;                    // -> NaN/Inf
	    else if( exp != 0 )                   // normalized value
	    {
	        exp += 0x1c000;                   // exp - 15 + 127
	        if( mant == 0 && exp > 0x1c400 )  // smooth transition
	            return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
	                                            | exp << 13 | 0x3ff );
	    }
	    else if( mant != 0 )                  // && exp==0 -> subnormal
	    {
	        exp = 0x1c400;                    // make it normal
	        do {
	            mant <<= 1;                   // mantissa * 2
	            exp -= 0x400;                 // decrease exp by 1
	        } while( ( mant & 0x400 ) == 0 ); // while not normal
	        mant &= 0x3ff;                    // discard subnormal bit
	    }                                     // else +/-0 -> +/-0
	    return Float.intBitsToFloat(          // combine all parts
	        ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
	        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
	}

	// returns all higher 16 bits as 0 for all results
	public static int fromFloat( float fval )
	{
	    int fbits = Float.floatToIntBits( fval );
	    int sign = fbits >>> 16 & 0x8000;          // sign only
	    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

	    if( val >= 0x47800000 )               // might be or become NaN/Inf
	    {                                     // avoid Inf due to rounding
	        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
	        {                                 // is or must become NaN/Inf
	            if( val < 0x7f800000 )        // was value but too large
	                return sign | 0x7c00;     // make it +/-Inf
	            return sign | 0x7c00 |        // remains +/-Inf or NaN
	                ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
	        }
	        return sign | 0x7bff;             // unrounded not quite Inf
	    }
	    if( val >= 0x38800000 )               // remains normalized value
	        return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
	    if( val < 0x33000000 )                // too small for subnormal
	        return sign;                      // becomes +/-0
	    val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
	    return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
	         + ( 0x800000 >>> val - 102 )     // round depending on cut off
	      >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
	}


	public static void main(String[] args) throws IOException {
		if(args.length>0) {
			if(args[0].contentEquals("3")) {
				int k=Integer.parseInt(args[3]);
				File f= new File(args[1]);
		Scanner sc = new Scanner(f);
		int width, height, grayscale;
		width=sc.nextInt();
		height=sc.nextInt();
		grayscale=sc.nextInt();
		System.out.println("w: "+width+" h: "+height+" c: "+grayscale);
		sc.close();

		File mf=new File("image_b.pgm.SVD");
		if (!mf.exists()) {
            try {
				mf.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		OutputStream opStream = null;
        opStream = new FileOutputStream(mf);
        byte[] byteContent = "P2".getBytes();
        opStream.write(byteContent);
        String lineSeparator = System.getProperty("line.separator");
        opStream.write(lineSeparator.getBytes());

        String strContent = String.valueOf(width);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(' ');

        strContent = String.valueOf(height);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(' ');

        strContent = String.valueOf(grayscale);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(lineSeparator.getBytes());

        strContent = String.valueOf(k);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(lineSeparator.getBytes());


		f=new File(args[2]);
		/*Scanner in=new Scanner(f);

		int words=0;
		while(in.hasNext())
        {
            in.next();
            words++;
        }

		System.out.println("no of floats= "+words);
		PrintStream o = new PrintStream(new File("A.txt"));

	     // Store current System.out before assigning a new value


	     // Assign o to output stream
	     System.setOut(o); */
	     sc=new Scanner(f);
	     //int count=0;
	     //String s;
		//System.out.println(sc.nextFloat());

		short[][] arrU= new short[height][height]; //in
	    //float[][] arrU= new float[height][height];
		for (int i=0;i<height;i++) {
			for(int j=0;j<height;j++) {
				//s=sc.next();

				float aij_f=sc.nextFloat(); //in

				//System.out.println("i "+i+" j "+j+" float "+aij_f);
				int aij_int=fromFloat(aij_f); //in
				arrU[i][j]=(short)aij_int; //in
				//arrU[i][j]=sc.nextFloat();
				//count++;
						/*strContent = String.valueOf(aij_int);
				        byteContent=strContent.getBytes();
				        opStream.write(byteContent);
				        opStream.write(lineSeparator.getBytes());*/
				}

			}//opStream.write(lineSeparator.getBytes());

		short[][] arrS= new short[height][width]; //in
		//float[][] arrS= new float[height][width];
		for (int i=0;i<height;i++) {
			for(int j=0;j<width;j++) {
				//s=sc.next();
				float aij_f=sc.nextFloat(); //in

				int aij_int=fromFloat(aij_f); //in
				arrS[i][j]=(short)aij_int; //in
				//arrS[i][j]=sc.nextFloat();
				//count++;
				//System.out.println("i "+i+" j "+j+" float "+aij_f+" c: "+count);
						/*strContent = String.valueOf(aij_int);
				        byteContent=strContent.getBytes();
				        opStream.write(byteContent);
				        opStream.write(lineSeparator.getBytes());*/
			}

		}
		//float [][] arrV= new float[width][width];
		//float [][] arrVT= new float[width][width];
		short[][] arrV= new short[width][width]; //in
		//int [][] arrVT=new int[width][width]; //in
		for (int i=0;i<width;i++) {
			for(int j=0;j<width;j++) {
				//s=sc.next();
				float aij_f=sc.nextFloat();; //in
				//System.out.println("i "+i+" j "+j+" float "+aij_f);
				int aij_int=fromFloat(aij_f); //in
				arrV[i][j]=(short)aij_int; //in
				//arrV[i][j]=sc.nextFloat();

				//count++;
						/*strContent = String.valueOf(aij_int);
				        byteContent=strContent.getBytes();
				        opStream.write(byteContent);
				        opStream.write(lineSeparator.getBytes());*/
		     }

	    }
		sc.close();
		//PrintStream Console=System.out;
		//System.setOut(Console);
		/*for (int i=0;i<width;i++) {
			for(int j=0;j<width;j++) {
				arrVT[i][j]=arrV[j][i];

			}
		}
		*/
		//ByteBuffer buf = ByteBuffer.allocate(10);
		/*PrintStream o1=new PrintStream(new File("C:\\Users\\sb343\\Pictures\\imm.txt"));
        System.setOut(o1);
        System.out.println("P2");
        System.out.println(width+" "+height);
        System.out.println(grayscale);
        System.out.println(k);*/

		for(int i=0;i<height;i++) {
			for(int j=0;j<k;j++) {//k
				//System.out.print(arrU[i][j]+" ");
				strContent = String.valueOf(arrU[i][j]);
		        byteContent=strContent.getBytes();
		        opStream.write(byteContent);
		        opStream.write(' ');
		        //opStream.write(lineSeparator.getBytes());
			}
		}
		for(int i=0;i<k;i++) {//k
			for(int j=0;j<k;j++) {//k
				//System.out.print(arrS[i][j]+" ");
				strContent = String.valueOf(arrS[i][j]);
		        byteContent=strContent.getBytes();
		        opStream.write(byteContent);
		        opStream.write(' ');
		        //opStream.write(lineSeparator.getBytes());
			}
		}

		for(int i=0;i<k;i++) {//k
			for(int j=0;j<width;j++) {
				//System.out.print(arrV[i][j]+" ");
				strContent = String.valueOf(arrV[i][j]);
		        byteContent=strContent.getBytes();
		        opStream.write(byteContent);
		        opStream.write(' ');
		        //opStream.write(lineSeparator.getBytes());
			}
		}
		opStream.flush();
		opStream.close();
		//o1.flush();
		//o1.close();
			}
			else if(args[0].equals("4")) {
		Scanner sc1=new Scanner(new File(args[1]));
		String mn=sc1.next();
		System.out.println("mn: "+mn);

		int width=sc1.nextInt();
		System.out.println("w: "+width);
		int height=sc1.nextInt();
		System.out.println("h: "+height);
		int grayscale=sc1.nextInt();
		System.out.println("gs: "+grayscale);
		int k=sc1.nextInt();
		System.out.println("k: "+k);
		/*
		File mf1=new File("C:\\Users\\sb343\\Pictures\\img.txt");
		opStream = new FileOutputStream(mf1);
        byteContent = mn.getBytes();
        opStream.write(byteContent);
        lineSeparator = System.getProperty("line.separator");
        opStream.write(lineSeparator.getBytes());

        strContent = String.valueOf(width);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(' ');

        strContent = String.valueOf(height);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(lineSeparator.getBytes());

        strContent = String.valueOf(grayscale);
        byteContent=strContent.getBytes();
        opStream.write(byteContent);
        opStream.write(lineSeparator.getBytes());

        ByteBuffer buf = ByteBuffer.allocate(2048);

        FileInputStream in = new FileInputStream(mf);
        int len = in.getChannel().read(buf);
        RandomAccessFile aFile = new RandomAccessFile
                ("C:\\Users\\sb343\\Pictures\\imm.txt", "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(inChannel.read(buffer) > 0)
        {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++)
            {
                System.out.print((char) buffer.get());
            }
            buffer.clear(); // do something with the data and clear/compact it.
        }*/
        //byte b=(byte) sc1.nextByte(10);
        //int ff= b;
        //System.out.println("b "+b+" ff "+ff);

		float[][] arr_fU=new float[height][k]; //change 2nd ht to k
		float[][] arr_fS=new float[k][k]; //change both to k
		float[][] arr_fVT=new float[k][width]; //change 1st wi to k
		int ff=0;
		for(int i=0;i<height;i++) {
			for(int j=0;j<k;j++){ //k
				//arr_fU[i][j]=sc1.nextFloat();
				ff=(int)sc1.nextInt(); //in
				arr_fU[i][j]=toFloat(ff); //in
		        //opStream.write(lineSeparator.getBytes());
			}
		}
		for(int i=0;i<k;i++) {//k
			for(int j=0;j<k;j++) {//k
				//arr_fS[i][j]=sc1.nextFloat();
				ff=(int)sc1.nextInt(); //in
				arr_fS[i][j]=toFloat(ff); //in
		        //opStream.write(lineSeparator.getBytes());
			}
		}

		for(int i=0;i<k;i++) {//k
			for(int j=0;j<width;j++) {
				//arr_fVT[i][j]=sc1.nextFloat();
				ff=(int)sc1.nextInt(); //in
				arr_fVT[i][j]=toFloat(ff); //in
		        //opStream.write(lineSeparator.getBytes());
			}
		}
		float[][] product = new float[k][width]; //width na k
		for(int i = 0; i < k; i++) {
            for (int j = 0; j < width; j++) {//k
            	product[i][j]=(float) 0.0;
            }
		}
        for(int i = 0; i < k; i++) {
            for (int j = 0; j < width; j++) {//k thik koro
                for (int p = 0; p < k; p++) {//k thik koro
                    product[i][j] += arr_fS[i][p] * arr_fVT[p][j];
                }
            }
        }

        float[][] A = new float[height][width];
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	A[i][j]=(float) 0.0;
            }
            }

        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int p = 0; p < k; p++) {//k thik korte hbe
                    	A[i][j] += arr_fU[i][p] * product[p][j];
                }
            }
        }
        PrintStream o=new PrintStream(new File("image_k.PGM"));
        System.setOut(o);
        System.out.println(mn);
        System.out.println(width+" "+height);
        System.out.println(grayscale);
        for(int i = 0; i < height; i++) {
        	for(int j = 0; j < width; j++){
        		if(A[i][j]-(int)A[i][j]<0.1) {
        		    System.out.print((int)A[i][j]+" ");
        		    }
        		else {
        			System.out.print((int)Math.ceil(A[i][j])+" ");
        		}
        		/*strContent = String.valueOf(A[i][j]);
		        byteContent=strContent.getBytes();
		        opStream.write(byteContent);
		        opStream.write(' ');*/
        	}System.out.println();
        }
		}
        //opStream.flush();
		//opStream.close();
		//float a =(float)1.22;
		//int kj=fromFloat(a);
		//System.out.println("int: "+kj+" f: "+a);

		//System.out.println(toFloat(kj));
		//a=a*(float)44.444;
		//kj=fromFloat(a);
		//System.out.println(toFloat(kj));
	}



	}


}
