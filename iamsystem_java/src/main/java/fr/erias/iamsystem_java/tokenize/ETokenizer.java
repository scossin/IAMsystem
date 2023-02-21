package fr.erias.iamsystem_java.tokenize;

public enum ETokenizer
{
	FRENCH
	{
		@Override
		public TokenizerImp getInstance()
		{
			return new TokenizerImp(NormFunctions.lowerNoAccents, SplitFunctions.splitAlphaNum);
		}
	},
	ENGLISH
	{
		@Override
		public TokenizerImp getInstance()
		{
			return new TokenizerImp(NormFunctions.lowerCase, SplitFunctions.splitAlphaNum);
		}
	};

	private ETokenizer()
	{
	};

	public abstract TokenizerImp getInstance();
}
