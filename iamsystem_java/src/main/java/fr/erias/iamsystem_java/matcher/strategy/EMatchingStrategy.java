package fr.erias.iamsystem_java.matcher.strategy;

public enum EMatchingStrategy
{

	NoOverlapStrategy
	{
		@Override
		public IMatchingStrategy getInstance()
		{
			return new NoOverlapMatching();
		}
	},

	WindowStrategy
	{
		@Override
		public IMatchingStrategy getInstance()
		{
			return new WindowMatching();
		}
	},

	LargeWindowStrategy
	{
		@Override
		public IMatchingStrategy getInstance()
		{
			return new LargeWindowMatching();
		}
	};

	private EMatchingStrategy()
	{

	};

	public abstract IMatchingStrategy getInstance();
}
