package org.scribble.ext.f17.sesstype.name;

import org.scribble.sesstype.kind.NonRoleArgKind;
import org.scribble.sesstype.kind.PayloadTypeKind;
import org.scribble.sesstype.name.PayloadType;


//public class AnnotDataType<K> extends DataType implements AnnotType
public class AnnotPayloadType<K extends PayloadTypeKind> implements PayloadType<K>, AnnotType
{
	//private static final long serialVersionUID = 1L;
	
	public final PayloadType<K> pay;
	public final PayloadVar annot;  // Cf. AnnotUnaryPayloadElem

	public AnnotPayloadType(PayloadType<K> pay, PayloadVar annot)
	{
		this.pay = pay;
		this.annot = annot;
	}
	
	@Override
	public String toString()
	{
		return this.annot + ":" + this.pay;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof AnnotPayloadType))
		{
			return false;
		}

		AnnotPayloadType<?> them = (AnnotPayloadType<?>) o;
		return them.canEqual(this) && this.pay.equals(them.pay) && this.annot.equals(them.annot);
	}
	
	public boolean canEqual(Object o)
	{
		return o instanceof AnnotPayloadType;
	}

	@Override
	public int hashCode()
	{
		int hash = 3163;
		hash = 31 * super.hashCode();
		hash = 31 * this.pay.hashCode();
		hash = 31 * this.annot.hashCode();
		return hash;
	}

	@Override
	public NonRoleArgKind getKind()
	{
		return this.pay.getKind();
	}

	/*private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(this.proto);
		out.writeObject(this.role);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.proto = (GProtocolName) in.readObject();
		this.role = (Role) in.readObject();
	}*/
}
