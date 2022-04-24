package fr.erias.IAMsystemFR.synonyms;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * French Soundex implemented by Harold Capitaine (https://github.com/hcapitaine)
 * The code was extracted from this repository https://github.com/hcapitaine/french-phonetic-analyser
 * The license of this code is GNU General Public License v3.0
 * Conditions of the licence : 
 *  License and copyright notice
 *  State changes : I replaced 'public static' by 'private' and added 'implements StringEncoder'
 *  Disclose source : https://github.com/hcapitaine/french-phonetic-analyser
 *  Same license : GNU General Public License v3.0 license of IAMsystemFR
 *  
 *   
 * @author Sebastien Cossin
 */


//1 for in sound
//2 for é sound
//3 for an sound
//4 for on sound
//5 for S sound
//8 for oeu/eu
public class CapitaineSoundex implements StringEncoder {

	private final List<Character> VOWELS = Arrays.asList('A', 'E', '2', 'I', 'O', 'U', 'Y');

	private final List<Character> MUTED_ENDED_CONSONANT = Arrays.asList('C', 'D', 'H', 'G', 'P', 'S', 'T', 'X', 'Z');

	private final List<Character> NOT_ALWAYS_MUTED_ENDED_CONSONANT = Arrays.asList('C', 'D', 'G', 'P', 'S', 'X', 'Z');

	private final List<Character> MUTED_S_PRECEDING_VOWEL = Arrays.asList('A', 'O');

	private final List<Character> MUTED_S_FOLLOWING_CONSONANT = Arrays.asList('B', 'J', 'M');

	private final List<Character> DOUBLE_CONSONANT = Arrays.asList('C', 'P', 'R', 'T', 'Z', 'N', 'M', 'G', 'L', 'F');

	private final List<Character> SOUND_2_ACCENTUATED_CHARS = Arrays.asList('É', 'È', 'Ê', 'Ë');

	private Set<String> getSet(String value){
		return new HashSet<>(Arrays.asList(value));
	}
	
	private final String sep = ";";
	
	public String getSeparator() {
		return(sep);
	}
	
	@Override
	public String encode(String source) throws EncoderException {
		return String.join(this.sep, encodeStr(source));
	}

	private List<String> encodeStr(String input){
		if (input == null || input.length() == 0) {
			return Arrays.asList(input);
		}
		int len = input.length();
		String upperStr = input.toUpperCase(Locale.FRENCH);

		char[] chars = new char[len];
		int count = 0;
		for (int i = 0; i < len; i++) {
			if (Character.isLetter(upperStr.charAt(i))) {
				if (SOUND_2_ACCENTUATED_CHARS.contains(upperStr.charAt(i))) {
					chars[count++] = '2';
				} else {
					chars[count++] = upperStr.charAt(i);
				}
			}
		}
		char[] res = new char[count];
		int finalSize = ASCIIFoldingFilter.foldToASCII(chars, 0, res, 0, count);
		String cleanedString = new String(chars, 0, finalSize);
		return new ArrayList<>(operatePhonetic("", charAt(cleanedString, 0), substring(cleanedString, 1, cleanedString.length())));
	}

	private Set<String> operatePhonetic(String acc, Character c, String tail) {

		if (c == null) {
			return getSet(acc);
		}

		if (tail == null || tail.isEmpty()) {

			//Trailing muted consonant
			if (MUTED_ENDED_CONSONANT.contains(c)) {
				if (c != 'X') {
					Set<String> encodedTokens = operatePhonetic(
							substring(acc, 0, acc.length() - 1),
							charAt(acc, acc.length() - 1),
							""
							);
					if(NOT_ALWAYS_MUTED_ENDED_CONSONANT.contains(c)){
						if(c == 'C'){
							encodedTokens.add(acc+'K');
						} else {
							encodedTokens.add(acc+c);    
						}

					}
					return encodedTokens;
				} else {

					//X not pronounced when preceedeed with 8 sound, ou/oi sound, O sound
					if (charAt(acc, acc.length() - 1) == Character.valueOf('8')
							|| charAt(acc, acc.length() - 1) == Character.valueOf('O')
							|| (acc.length() >= 2 && "OU".equals(substring(acc, acc.length() - 2, acc.length())))
							|| (
									acc.length() >= 2
									&& "OI".equals(substring(acc, acc.length() - 2, acc.length()))
									)
							|| (
									acc.length() >= 3
									&& !VOWELS.contains(charAt(acc, acc.length() - 3))
									&& "RI".equals(substring(acc, acc.length() - 2, acc.length()))
									)
							) {
						Set<String> encodedTokens = getSet(acc);
						encodedTokens.add(acc+'X');
						return encodedTokens;
					}
				}
			}

		} else {

			//Muted starting H with vowels
			if ((acc.isEmpty() || (charAt(acc, acc.length() - 1) != Character.valueOf('C') && charAt(acc, acc.length() - 1) != Character.valueOf('S'))) && c == 'H') {
				if (VOWELS.contains(charAt(tail, 0))) {
					return operatePhonetic(acc, charAt(tail, 0), substring(tail, 1, tail.length()));
				}
			}

			//Vowel followed by h
			if (!acc.isEmpty() && VOWELS.contains(charAt(acc, acc.length() - 1)) && c == 'H') {
				return operatePhonetic(acc, charAt(tail, 0), substring(tail, 1, tail.length()));
			}

			//SC as S or as SK
			if(c == 'S' && tail.length() >=2 && charAt(tail, 0) == Character.valueOf('C') && (charAt(tail, 1) == Character.valueOf('E') ||charAt(tail, 1) == Character.valueOf('I') || charAt(tail, 1) == Character.valueOf('Y'))){
				Set<String> encodedToken = operatePhonetic(acc + '5', charAt(tail, 1), substring(tail, 2, tail.length()));
				encodedToken.addAll(operatePhonetic(acc + "SK", charAt(tail, 1), substring(tail, 2, tail.length())));
				return encodedToken;
			}

			//C as S
			if (c == 'C' && (charAt(tail, 0) == Character.valueOf('E') || charAt(tail, 0) == Character.valueOf('I') || charAt(tail, 0) == Character.valueOf('Y'))) {
				return operatePhonetic(acc + '5', charAt(tail, 0), substring(tail, 1, tail.length()));
			} else {
				if(c == 'C' && charAt(tail, 0) == Character.valueOf('H')){

					if(charAt(tail, 1) == null){
						HashSet<String> encodedTokens = new HashSet<>();
						encodedTokens.add(acc+"CH");
						encodedTokens.add(acc+"K");
						return encodedTokens;
					} else {
						//CH followed by 2 vowels as K
						if (VOWELS.contains(charAt(tail, 1)) && VOWELS.contains(charAt(tail, 2))) {
							return operatePhonetic(acc + 'K', charAt(tail, 1), substring(tail, 2, tail.length()));
						} else {
							//CHR / CHL as KR / KL
							if ((charAt(tail, 1) == Character.valueOf('R') || charAt(tail, 1) == Character.valueOf('L')) && VOWELS.contains(charAt(tail, 2))) {
								return operatePhonetic(acc + 'K', charAt(tail, 1), substring(tail, 2, tail.length()));
							}
							return operatePhonetic(acc + "C", charAt(tail, 0), substring(tail, 1, tail.length()));
						}
					}
				}
				//C as K
				if (c == 'C' && charAt(tail, 0) != Character.valueOf('E') && charAt(tail, 0) != Character.valueOf('I') && charAt(tail, 0) != Character.valueOf('Y') && charAt(tail, 0) != Character.valueOf('H')) {
					if (!acc.isEmpty() && charAt(acc, acc.length() - 1) == Character.valueOf('K') || charAt(tail, 0) == Character.valueOf('K')) {
						return operatePhonetic(acc, charAt(tail, 0), substring(tail, 1, tail.length()));
					} else {
						return operatePhonetic(acc + 'K', charAt(tail, 0), substring(tail, 1, tail.length()));
					}
				}
			}

			if (c == 'O' && tail.length() >= 2 && charAt(tail, 0) == Character.valueOf('E') && charAt(tail, 1) == Character.valueOf('U')) {
				return operatePhonetic(acc + "8", charAt(tail, 2), substring(tail, 3, tail.length()));
			}

			if (c == 'E' && charAt(tail, 0) == Character.valueOf('U')) {
				return operatePhonetic(acc + "8", charAt(tail, 1), substring(tail, 2, tail.length()));
			}

			//G as J : ge, gi, gy, gé, gè, gë, gê
			if (c == 'G' && (charAt(tail, 0) == Character.valueOf('E') || charAt(tail, 0) == Character.valueOf('I') || charAt(tail, 0) == Character.valueOf('Y') || charAt(tail, 0) == Character.valueOf('2'))) {
				return operatePhonetic(acc + 'J', charAt(tail, 0), substring(tail, 1, tail.length()));
			}


			if (c == 'S') {
				//S as Z
				if (!acc.isEmpty() && VOWELS.contains(charAt(acc, acc.length() - 1)) && VOWELS.contains(charAt(tail, 0))) {
					return operatePhonetic(acc + 'Z', charAt(tail, 0), substring(tail, 1, tail.length()));
				} else if (charAt(tail, 0) == Character.valueOf('S')) { // SS as 5
					if (tail.length() > 1)
						return operatePhonetic(acc + '5', charAt(tail, 1), substring(tail, 2, tail.length()));
					else
						return getSet(acc + '5');
				} else if (acc.length() > 0 && MUTED_S_PRECEDING_VOWEL.contains(charAt(acc, acc.length() - 1))
						&& tail.length() > 1 && MUTED_S_FOLLOWING_CONSONANT.contains(charAt(tail, 0))) { // Muted S
					return operatePhonetic(acc, charAt(tail, 0), substring(tail, 1, tail.length()));
				} else { //S as 5
					return operatePhonetic(acc + '5', charAt(tail, 0), substring(tail, 1, tail.length()));
				}
			}


			//W as V
			if (c == 'W') {
				return operatePhonetic(acc + 'V', charAt(tail, 0), substring(tail, 1, tail.length()));
			}

			//remove double consonant ex
			if (isDoubleConsonnant(c, tail)) {
				return operatePhonetic(acc, c, substring(tail, 1, tail.length()));
			}

			//Q, QU as K
			if (c == 'Q' && charAt(tail, 0) == Character.valueOf('U')) {
				return operatePhonetic(acc + 'K', charAt(tail, 1), substring(tail, 2, tail.length()));
			}

			//PH as F
			if (c == 'P' && charAt(tail, 0) == Character.valueOf('H')) {
				return operatePhonetic(acc + 'F', charAt(tail, 1), substring(tail, 2, tail.length()));
			}


			Set<String> replacedThreeLettersINSound = replaceThreeLettersINSound(acc, c, tail, 'A', 'E');
			if (replacedThreeLettersINSound != null) {
				return replacedThreeLettersINSound;
			}

			//EAU as O
			if (c == 'E' && tail.length() >= 2 && charAt(tail, 0) == Character.valueOf('A') && charAt(tail, 1) == Character.valueOf('U')) {
				return operatePhonetic(acc + "O", charAt(tail, 2), substring(tail, 3, tail.length()));
			}

			Set<String> replacedTwoLettersSounds = replaceTwoLettersSounds(acc, c, tail);
			if (replacedTwoLettersSounds != null) {
				return replacedTwoLettersSounds;
			}

			Set<String> handleJeanCase = handleJEANSpecialCase(acc, c, tail);
			if (handleJeanCase != null) {
				return handleJeanCase;
			}


			if (c == 'T' && tail.length() >= 3 && VOWELS.contains(charAt(acc, acc.length() - 1)) && "ION".equals(substring(tail, 0, 3))) {
				return operatePhonetic(acc + "S", charAt(tail, 0), substring(tail, 1, tail.length()));
			}

			if(c == 'O' && "X".equals(tail)){
				return getSet(acc +"OX");
			}

		}

		if (c == 'E') {
			Set<String> nextAccs = operatePhonetic("2", charAt(tail, 0), substring(tail, 1, tail.length()));
			return nextAccs.stream().map(nextAcc->{
				if ("2".equals(nextAcc)) {
					return acc;
				} else {
					nextAcc = StringUtils.substringAfter(nextAcc, "2");
				}
				return acc + "2" + nextAcc;
			}).collect(Collectors.toSet());
		}

		//Y as I
		if (c == 'Y') {
			return operatePhonetic(acc + 'I', charAt(tail, 0), substring(tail, 1, tail.length()));
		}

		return operatePhonetic(acc + c, charAt(tail, 0), substring(tail, 1, tail.length()));
	}


	private boolean isDoubleConsonnant(Character c, String tail) {
		Character character = charAt(tail, 0);
		return c != null && character != null && DOUBLE_CONSONANT.contains(c.charValue()) && character.charValue() == c.charValue();
	}

	private Set<String> replaceTwoLettersSounds(String acc, char c, String tail) {

		//Trailing ER, ET and EZ as 2
		if (c == 'E' && tail.length() == 1 && (tail.charAt(0) == 'R' || tail.charAt(0) == 'T' || tail.charAt(0) == 'Z')) {
			Set<String> encodedTails = operatePhonetic("", charAt(tail, 1), substring(tail, 2, tail.length()));
			return encodedTails.stream().map(encodedTail -> {
				if (encodedTail.isEmpty()) {
					return acc + "2";
				} else {
					return acc + "2" + tail.charAt(0) + encodedTail;
				}
			}).collect(Collectors.toSet());
		}

		// AU as O
		if (c == 'A' && tail.charAt(0) == 'U') {
			return operatePhonetic(acc + 'O', charAt(tail, 1), substring(tail, 2, tail.length()));
		}


		Set<String> replacedAISound = replaceAISounds(acc, c, tail, 'A', 'E');
		if (replacedAISound != null) {
			return replacedAISound;
		}

		Set<String> replacedONSound = replaceONOrINOrANSound(acc, c, tail, "4", 'O');
		if (replacedONSound != null) {
			return replacedONSound;
		}


		Set<String> replacedINSound = replaceONOrINOrANSound(acc, c, tail, "1", 'Y', 'I', 'U');
		if (replacedINSound != null) {
			return replacedINSound;
		}

		Set<String> replacedANSound = replaceONOrINOrANSound(acc, c, tail, "3", 'A', 'E');
		if (replacedANSound != null) {
			return replacedANSound;
		}

		return null;
	}

	/**
	 * JEAN => J3
	 * JEAN* => JAN
	 *
	 * @return
	 */
	private Set<String> handleJEANSpecialCase(String acc, char c, String tail) {
		if (c == 'J' && tail.length() > 2 && tail.charAt(0) == 'E' && tail.charAt(1) == 'A' && tail.charAt(2) == 'N') { // at least 3 trailing chars : EAN
			if (tail.length() == 3) {
				return getSet(acc + "J3");
			} else {
				return operatePhonetic(acc + "JA", tail.charAt(2), substring(tail, 3, tail.length()));
			}
		}
		return null;
	}

	private  Set<String> replaceAISounds(String acc, char c, String tail, Character... firstLetters) {
		if (Arrays.asList(firstLetters).contains(c) && (tail.charAt(0) == 'I' || tail.charAt(0) == 'Y')) {
			acc += "2";
			if (tail.charAt(0) == 'Y' && tail.length() != 1 && (c != 'E' || c == 'E' && VOWELS.contains(tail.charAt(1)))) {
				acc += "I";
			}
			//X is not pronounced in words ending with AIX
			if(c == 'A' && charAt(tail, 0) == 'I' && "X".equals(substring(tail, 1, tail.length()))){
				return getSet(acc);
			}
			return operatePhonetic(acc, charAt(tail, 1), substring(tail, 2, tail.length()));
		}
		return null;
	}

	private Set<String> replaceThreeLettersINSound(String acc, char c, String tail, Character... firstLetters) {
		if (Arrays.asList(firstLetters).contains(c) && tail.length() >= 2 &&
				tail.charAt(0) == 'I' && (tail.charAt(1) == 'N' || tail.charAt(1) == 'M')) {
			if (tail.length() >= 3 && (tail.charAt(2) == 'M' || tail.charAt(2) == 'N' || tail.charAt(2) == 'H' || VOWELS.contains(tail.charAt(2)))) {
				return replaceTwoLettersSounds(acc, c, tail);
			}
			return operatePhonetic(acc + "1", charAt(tail, 2), substring(tail, 3, tail.length()));
		}
		return null;
	}

	private  Set<String> replaceONOrINOrANSound(String acc, char c, String tail, String replaceValue, Character... firstLetters) {
		if (Arrays.asList(firstLetters).contains(c)) {
			if (tail.charAt(0) == 'N' || tail.charAt(0) == 'M') {
				if (tail.length() > 1 && (tail.charAt(1) == 'N' || tail.charAt(1) == 'M' || tail.charAt(1) == 'H' || VOWELS.contains(tail.charAt(1)))) {
					return operatePhonetic(acc + c, tail.charAt(0), substring(tail, 1, tail.length()));
				}
				return operatePhonetic(acc + replaceValue, charAt(tail, 1), substring(tail, 2, tail.length()));
			}
		}
		return null;
	}

	private String substring(String tail, int startIndex, int endIndex) {
		if (tail == null) {
			return "";
		}
		if (tail.length() <= startIndex) {
			return "";
		} else {
			if (endIndex > tail.length() - 1) {
				return tail.substring(startIndex, tail.length());
			} else {
				return tail.substring(startIndex, endIndex);
			}
		}
	}

	private Character charAt(String tail, int position) {
		if (tail == null || tail.isEmpty() || position < 0 || position >= tail.length()) {
			return null;
		}
		return tail.charAt(position);
	}

	@Override
	public Object encode(Object source) throws EncoderException {
		return new EncoderException();
	}
}
