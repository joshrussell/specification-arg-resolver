/**
 * Copyright 2014-2015 the original author or authors.
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
package net.kaczmarzyk.spring.data.jpa.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * @author Tomasz Kaczmarzyk
 */
public class AnnotatedSpecInterfaceArgumentResolverTest extends ResolverTestBase {

	AnnotatedSpecInterfaceArgumentResolver resolver = new AnnotatedSpecInterfaceArgumentResolver();
	
	public static interface IfaceWithoutAnnotations extends Specification<Object> {
	}

	@Spec(path = "name", spec = Like.class)
	public static interface IfaceWithSimpleSpec extends Specification<Object> {
	}
	
	@Spec(path = "name", spec = Like.class)
	public static interface IfaceNotExtendingSpecification {
	}
	
	@And({ @Spec(path = "name", spec = Like.class) })
	public static interface IfaceWithConjunction extends Specification<Object> {
	}
	
	@And({ @Spec(path = "name", spec = Like.class) })
	@Spec(path = "name", spec = Like.class)
	public static interface OverAnnotatedInterface extends Specification<Object> {
	}
	
	@Or({ @Spec(path = "name", spec = Like.class) })
	public static interface IfaceWithDisjunction extends Specification<Object> {
	}
	
	@Spec(path = "name", spec = Like.class)
	public static class Clazz extends Like<Object> {
		public Clazz(String path, String[] args) {
			super(path, args);
		}
	}
	
	public static class TestController {
		public void methodWithSimpleSpec(IfaceWithSimpleSpec arg) {}
		public void methodWithClass(Clazz arg) {}
		public void methodWithOverannotatedSpec(OverAnnotatedInterface arg) {}
		public void methodWithSpecWithoutAnnotations(IfaceWithoutAnnotations arg) {}
		public void methodWithNonSpec(IfaceNotExtendingSpecification arg) {}
		public void methodWithConjunction(IfaceWithConjunction arg) {}
		public void methodWithDisjunction(IfaceWithDisjunction arg) {}
	}
	
	NativeWebRequest req = mock(NativeWebRequest.class);
	
	@Before
	public void init() {
		when(req.getParameterValues("name")).thenReturn(new String[] { "Homer" });
	}
	
	@Test
	public void doesNotSupportTypeThatDoesntExtendSpecification() {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithNonSpec", IfaceNotExtendingSpecification.class), 0);
        
        assertFalse(resolver.supportsParameter(param));
	}
	
	@Test
	public void doesNotSupportIfaceWithoutAnnotationsSpecification() {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithSpecWithoutAnnotations", IfaceWithoutAnnotations.class), 0);
        
        assertFalse(resolver.supportsParameter(param));
	}
	
	@Test
	public void doesNotSupportClasses() {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithClass", Clazz.class), 0);
        
        assertFalse(resolver.supportsParameter(param));
	}
	
	@Test
	public void supportsIterfaceWithSimpleSpec() throws Exception {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithSimpleSpec", IfaceWithSimpleSpec.class), 0);
        
		assertTrue(resolver.supportsParameter(param));
		assertThat(resolver.resolveArgument(param, null, req, null)).isInstanceOf(IfaceWithSimpleSpec.class);
	}
	
	@Test
	public void supportsIterfaceWithConjunctionSpec() throws Exception {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithConjunction", IfaceWithConjunction.class), 0);
        
		assertTrue(resolver.supportsParameter(param));
		assertThat(resolver.resolveArgument(param, null, req, null)).isInstanceOf(IfaceWithConjunction.class);
	}
	
	@Test
	public void supportsIterfaceWithDisjunctionSpec() throws Exception {
		MethodParameter param = MethodParameter.forMethodOrConstructor(testMethod("methodWithDisjunction", IfaceWithDisjunction.class), 0);
        
        assertTrue(resolver.supportsParameter(param));
        assertThat(resolver.resolveArgument(param, null, req, null)).isInstanceOf(IfaceWithDisjunction.class);
	}

	@Override
	protected Class<?> controllerClass() {
		return TestController.class;
	}
}
