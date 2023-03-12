package fr.erias.iamsystem.tokenize;

public enum ETokenizer
{
	FRENCH
	{
		@Override
		public TokenizerImp getInstance()
		{
			return new TokenizerImp(NormFunctions.normFrench, SplitFunctions.splitAlphaNum);
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
