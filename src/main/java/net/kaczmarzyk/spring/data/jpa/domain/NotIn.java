/**
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk.spring.data.jpa.domain;

import net.kaczmarzyk.spring.data.jpa.utils.Converter;

import javax.persistence.criteria.*;
import java.util.Arrays;

/**
 * <p>Filters with "in" where-clause (e.g. {@code where firstName in ("Homer", "Marge")}).</p>
 * 
 * <p>Values to match against should be provided as multiple values of a single HTTP parameter, eg.: 
 *  {@code GET http://myhost/customers?firstName=Homer&firstName=Marge}.</p>
 * 
 * <p>Supports multiple field types: strings, numbers, booleans, enums, dates.</p>
 * 
 * @author Tomasz Kaczmarzyk
 * @author Maciej Szewczyszyn
 */
public class NotIn<T> extends PathSpecification<T> {

	private String[] allowedValues;
	private Converter converter;

	public NotIn(String path, String[] httpParamValues) {
		this(path, httpParamValues, null);
	}

	public NotIn(String path, String[] httpParamValues, String[] config) {
		super(path);
		if (httpParamValues == null || httpParamValues.length < 1) {
			throw new IllegalArgumentException();
		}
		if (config != null && config.length != 1) {
			throw new IllegalArgumentException("config may contain only one value (date format), but was: " + config);
		}
		String dateFormat = config != null ? config[0] : null;
		allowedValues = httpParamValues;
		converter = Converter.withDateFormat(dateFormat);
	}
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Path<?> path = path(root);
		Class<?> typeOnPath = path.getJavaType();
		return cb.not(path.in(converter.convert(Arrays.asList(allowedValues), typeOnPath)));
	}

}
