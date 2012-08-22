package exception;

public class ErrException extends Exception
{
	public ErrException(String msg)
	{
		super(msg);
	}
	public ErrException(String msg,Throwable e)
	{
		super(msg,e);
	}
}
