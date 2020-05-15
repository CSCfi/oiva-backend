/*
 * Copyright 2002-2015 the original author or authors.
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

package fi.minedu.oiva.backend.core.spring.handler;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.concurrent.CompletionStage;

/**
 * Support for {@link CompletionStage} as a return value type.
 *
 * @see org.springframework.web.servlet.mvc.method.annotation.ListenableFutureReturnValueHandler
 * <p>
 * Note: Spring 4.2 will support more specific CompletableFuture return type
 */
public class CompletionStageReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(final MethodParameter returnType) {
        return CompletionStage.class.isAssignableFrom(returnType.getParameterType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleReturnValue(final Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        final DeferredResult<Object> deferredResult = new DeferredResult<>();
        WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(deferredResult, mavContainer);

        final CompletionStage<?> future = (CompletionStage<?>) returnValue;
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                deferredResult.setErrorResult(ex);
            } else {
                deferredResult.setResult(result);
            }
        });
    }
}
