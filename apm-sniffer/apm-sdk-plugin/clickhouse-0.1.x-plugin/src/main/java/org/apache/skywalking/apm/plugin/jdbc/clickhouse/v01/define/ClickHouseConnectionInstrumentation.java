/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

package org.apache.skywalking.apm.plugin.jdbc.clickhouse.v01.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * {@link ClickHouseConnectionInstrumentation} intercepts {@link ru.yandex.clickhouse.ClickHouseConnection}
 *
 * @author IluckySi
 */
public class ClickHouseConnectionInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String ENHANCE_CLASS = "ru.yandex.clickhouse.ClickHouseConnectionImpl";
    public static final String CREATE_STATEMENT_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.jdbc.clickhouse.v01.CreateStatementInterceptor";
    public static final String CREATE_PREPARED_STATEMENT_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.jdbc.clickhouse.v01.CreatePreparedStatmentInterceptor";
    public static final String CREATE_CALLABLE_STATEMENT_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.jdbc.clickhouse.v01.CreateCallableStatementInterceptor";

    @Override protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.CREATE_STATEMENT_METHOD_NAME);
                }

                @Override public String getMethodsInterceptor() {
                    return CREATE_STATEMENT_INTERCEPTOR_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            },

            new InstanceMethodsInterceptPoint() {
                @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.PREPARE_STATEMENT_METHOD_NAME);
                }

                @Override public String getMethodsInterceptor() {
                    return CREATE_PREPARED_STATEMENT_INTERCEPTOR_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            },

            new InstanceMethodsInterceptPoint() {
                @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.PREPARE_CALL_METHOD_NAME);
                }

                @Override public String getMethodsInterceptor() {
                    return CREATE_CALLABLE_STATEMENT_INTERCEPTOR_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            },

            new InstanceMethodsInterceptPoint() {
                @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.COMMIT_METHOD_NAME)
                        .or(named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.ROLLBACK_METHOD_NAME))
                        .or(named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.CLOSE_METHOD_NAME))
                        .or(named(org.apache.skywalking.apm.plugin.jdbc.define.Constants.RELEASE_SAVE_POINT_METHOD_NAME));
                }

                @Override public String getMethodsInterceptor() {
                    return org.apache.skywalking.apm.plugin.jdbc.define.Constants.SERVICE_METHOD_INTERCEPT_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    @Override protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }
}