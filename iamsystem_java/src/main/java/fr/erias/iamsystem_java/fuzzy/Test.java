package fr.erias.iamsystem_java.fuzzy;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;

public class Test
{

	public static void main(String[] args) throws EncoderException
	{
		StringEncoder encoder = new BeiderMorseEncoder();
		// inzufidstsanzi|inzufidstsonzi|inzufidzanzi|inzufidzdzanzi|inzufidzdzonzi|inzufidzonzi|inzufisanzi|inzufisonzi|inzufistsanzi|inzufistsdzanzi|inzufistsdzonzi|inzufistsonzi|inzufiststsanzi|inzufiststsonzi|inzufistzanzi|inzufistzonzi|inzufizanki|inzufizantsi|inzufizanzi|inzufizanzii|inzufizdstsanzi|inzufizdstsonzi|inzufizdzanzi|inzufizdzdzanzi|inzufizdzdzonzi|inzufizdzonzi|inzufizonki|inzufizontsi|inzufizonzi|inzufizonzii
		// inzufisanki|inzufisantsi|inzufisanzi|inzufisanzii|inzufisonki|inzufisontsi|inzufisonzi|inzufisonzii|inzufizanzi|inzufizanzii|inzufizonzi|inzufizonzii
		String encoded = encoder.encode("insuffisance"); // ANSFSNK111 ANSFSNK111
		System.out.println(encoded);
	}
}
