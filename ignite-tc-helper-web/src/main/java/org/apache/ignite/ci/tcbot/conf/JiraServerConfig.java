/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ci.tcbot.conf;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.ignite.ci.HelperConfig;
import org.apache.ignite.ci.conf.PasswordEncoder;
import org.apache.ignite.ci.jira.pure.Fields;
import org.apache.ignite.ci.jira.pure.Ticket;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 */
public class JiraServerConfig implements IJiraServerConfig {
    /** Service (server) Name. */
    private String code;

    /**
     * Tickets for commenting in JIRA and finding out PA tickets. Default project is "IGNITE".
     */
    private String projectCode;

    /**
     * Branch number prefix. Optional, if not present {@link #projectCode}-NNNNN is searched.<br>
     * But if branch has different enumeration, this prefix will be searched instead.<br>
     * If specified, that meant tickets maching branches have another identification.<br>
     * For exaple some ticket having ID {@link #projectCode}-N1 will be commented, but a branch will be identified using
     * {@link #branchNumPrefix}N2 with another number.<br><br>
     *
     * Search of branches will be performed using data in JIRA ticket fields for
     * {@link #projectCode}-N1, fields are listed in {@link Ticket} class.
     */
    private String branchNumPrefix;

    private Properties props;

    /**
     * JIRA Auth token to access, use {@link org.apache.ignite.ci.conf.PasswordEncoder#encodeJiraTok(String, String)}
     */
    private String authTok;

    /**
     * JIRA Server URL. HTTPs is highly recommended.
     */
    private String url;

    public JiraServerConfig() {
    }

    public JiraServerConfig(String code, Properties props) {
        this.code = code;
        this.props = props;
    }

    /** {@inheritDoc} */
    @Override public String getCode() {
        return code;
    }

    /**
     * @param props Properties.
     */
    public JiraServerConfig properties(Properties props) {
        this.props = props;

        return this;
    }

    /**
     * @param code Name.
     */
    public JiraServerConfig code(String code) {
        this.code = code;

        return this;
    }

    /** {@inheritDoc} */
    @Override public String getUrl() {
        if (Strings.isNullOrEmpty(url) && props != null)
            return props.getProperty(HelperConfig.JIRA_URL);

        return url;
    }

    /** {@inheritDoc} */
    @Override public String projectCodeForVisa() {
        if (Strings.isNullOrEmpty(projectCode) && props != null) {
            String ticketPref = props.getProperty(HelperConfig.JIRA_TICKET_TEMPLATE, "IGNITE-");

            return ticketPref.replaceAll("-", "");
        }

        return projectCode;
    }

    /** {@inheritDoc} */
    @Nullable @Override public String branchNumPrefix() {
        return Strings.emptyToNull(branchNumPrefix);
    }

    /**
     * Extracts JIRA basic authorization token from properties.
     *
     * @return Null or decoded auth token for Github.
     */
    @Nullable
    @Override
    public String decodedHttpAuthToken() {
        String tok;
        if (Strings.isNullOrEmpty(authTok) && props != null)
            tok = props.getProperty(HelperConfig.JIRA_AUTH_TOKEN);
        else
            tok = authTok;

        if (isNullOrEmpty(tok))
            return null;

        return PasswordEncoder.decode(tok);
    }
}
