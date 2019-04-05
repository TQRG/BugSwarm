/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Representation of an action a link is able to take.
 *
 * @author Greg Turnquist
 */
@Data
public class Affordance {

	private final String verb;
	private final boolean required;
	private final Map<String, Class<?>> properties;

	public Affordance() {
		this.verb = "";
		this.required = false;
		this.properties = new HashMap<String, Class<?>>();
	}

	public Affordance(Method method, Class<?> targetType, RequestMethod requestMethod) {

		switch (requestMethod) {
			case POST:
			case PUT:
				this.required = true;
				this.verb = requestMethod.toString();
				this.properties = new HashMap<String, Class<?>>();
				gatherInputRequirements(method, targetType);
				break;
			case PATCH:
				this.required = false;
				this.verb = requestMethod.toString();
				this.properties = new HashMap<String, Class<?>>();
				gatherInputRequirements(method, targetType);
				break;
			case GET:
			case HEAD:
			case DELETE:
			case OPTIONS:
			case TRACE:
			default:
				this.required = false;
				this.verb = requestMethod.toString();
				this.properties = new HashMap<String, Class<?>>();
				break;
		}
	}

	public static List<Affordance> byLink(Link link) {
		return link.getAffordances();
	}

	private void gatherInputRequirements(Method method, Class<?> targetType) {

		System.out.println("Must gather details about " + method.getName());

		for (int i = 0; i < method.getParameterTypes().length; i++) {
			for (Annotation annotation : method.getParameterAnnotations()[i]) {
				if (annotation.annotationType().equals(RequestBody.class)) {
					System.out.println("\t Spring MVC @RequestBody => " + method.getParameterTypes()[i]);
					for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(method.getParameterTypes()[i])) {
						if (!descriptor.getName().equals("class")) {
							System.out.println("\t\t\t" + descriptor.getName() + ", " + descriptor.getPropertyType());
							this.properties.put(descriptor.getName(), descriptor.getPropertyType());
						}
					}
				}
			}
		}

		System.out.println(this.toString());
	}
}
