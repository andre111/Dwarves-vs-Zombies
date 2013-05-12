package me.andre111.dvz.generator;

public class DvZGeneratorFunction {
	// Converts the degrees to a normalised radian for use with Math.sin()
	public static double normalise(long x) {

		long conv = x % 360;
		if(conv >= 1)
			x = x % 360;
		return Math.toRadians(x);
	}

	/*private static double sinOctave(int x, int z, int octave) {
		return (Math.sin(normalise(x/octave))+Math.sin(normalise(z/octave)));
	}

	private static double cube(double x) {
		return x*x*x;
	}*/

	public static double get(int x, int z, long seed) {
		//seed
		x += seed;
		z += seed;
		
		double calc;

		calc =
				//seed
				Math.sin(normalise(seed))
				
				+Math.sin(normalise(x/4))
				+Math.sin(normalise(z/5))
				+Math.sin(normalise(x-z))
				+(Math.sin(normalise(z))/(Math.sin(normalise(z))+2))*2
				+Math.sin(normalise((int) (Math.sin(normalise(x)+Math.sin(normalise(z)))+Math.sin(normalise(z))))+Math.sin(normalise(z/2))
						+Math.sin(normalise(x)
								+Math.sin(normalise(z)))
								+Math.sin(normalise(z))
								+Math.sin(normalise(z))
								+Math.sin(normalise(x)))
								+Math.sin(normalise((x+z)/4))
								+Math.sin(normalise((int) (x+Math.sin(normalise(z)*Math.sin(normalise(x))))));
				
		calc = Math.abs(calc/6);
		
		calc = calc * 0.40;
		calc = calc + 0.40;
		
		if(calc>1)
			calc = 1;
		
		return calc;
	}
}
