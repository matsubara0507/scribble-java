package org.scribble.ext.f17.ast;

import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.Op;

// Has op and payload (F17GConnect/MessageTransfer)
// FIXME: maybe refactor as abstract class (with op/pay fields)
public interface F17MessageAction
{
	Op getOp();
	Payload getPayload();
}
