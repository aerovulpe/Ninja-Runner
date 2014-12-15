package com.aerisvulpe.shinobi;

public class Duration{
	
	public static long[] get(int duration, int tiles){
		final long[] dur= new long[tiles];
		for(int i=0;i<tiles;i++){
			dur[i]= duration;
		}
		return dur;
	}
}