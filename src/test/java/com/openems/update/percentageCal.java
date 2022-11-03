package openems;

public class percentageCal {

	public static void main(String[] args) {

		getPer(855, 4010);

	}

	public static void getPer(int x, int y) {
		System.out.println(
					Math.floor(
				(((float) x / (float) y)) * 100 )

		);
	}

}
