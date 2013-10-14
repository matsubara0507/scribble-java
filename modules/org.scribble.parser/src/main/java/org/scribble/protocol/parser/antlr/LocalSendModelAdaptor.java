/*
 * Copyright 2009-11 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.protocol.parser.antlr;

import org.antlr.runtime.CommonToken;
import org.scribble.protocol.model.Message;
import org.scribble.protocol.model.Role;
import org.scribble.protocol.model.local.LSend;

/**
 * This class provides the model adapter for the 'send' parser rule.
 *
 */
public class LocalSendModelAdaptor implements ModelAdaptor {

	/**
	 * {@inheritDoc}
	 */
	public Object createModelObject(ParserContext context) {
		
		LSend ret=new LSend();

		context.pop(); // ';'

		ret.getToRoles().add(new Role(((CommonToken)context.pop()).getText()));
		
		while (context.peek() instanceof CommonToken
				&& ((CommonToken)context.peek()).getText().equals(",")) {
			context.pop(); // ','

			ret.getToRoles().add(0, new Role(((CommonToken)context.pop()).getText()));
		}
		
		context.pop(); // to
	
		ret.setMessage((Message)context.pop());
		
		context.push(ret);
			
		return ret;
	}

}