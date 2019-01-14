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
package org.apache.ignite.ci.jira.ignited;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import org.apache.ignite.ci.jira.Ticket;
import org.apache.ignite.ci.jira.pure.IJiraIntegration;
import org.apache.ignite.ci.teamcity.ignited.ITeamcityIgnited;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
class JiraIgnited implements IJiraIgnited {
    /** Pure HTTP Jira connection. */
    private IJiraIntegration jira;

    /** Jira ticket DAO. */
    @Inject private JiraTicketDao jiraTicketDao;

    /** Jira ticket Sync. */
    @Inject private JiraTicketSync jiraTicketSync;

    /** Server id. */
    private String srvId;

    /** Server id mask high. */
    private int srvIdMaskHigh;

    public void init(IJiraIntegration jira) {
        this.jira = jira;

        srvId = jira.getServiceId();

        srvIdMaskHigh = ITeamcityIgnited.serverIdToInt(srvId);

        jiraTicketDao.init();
    }

    /** {@inheritDoc} */
    @Override public String ticketPrefix() {
        return jira.ticketPrefix();
    }

    /** {@inheritDoc} */
    @NotNull @Override public String projectName() {
        return jira.projectName();
    }

    @Override public Set<Ticket> getTickets() {
        jiraTicketSync.ensureActualizeJiraTickets(taskName("actualizeJiraTickets"), srvId);

        return jiraTicketDao.getTickets(srvIdMaskHigh);
    }

    /**
     * @param taskName Task name.
     * @return Task name concatenated with server name.
     */
    @NotNull
    private String taskName(String taskName) {
        return ITeamcityIgnited.class.getSimpleName() + "." + taskName + "." + srvId;
    }

    /** {@inheritDoc} */
    @Override public String generateCommentUrl(String ticketFullName, int commentId) {
        return jira.generateCommentUrl(ticketFullName, commentId);
    }

    /** {@inheritDoc} */
    @Override public String generateTicketUrl(String id) {
        return jira.generateTicketUrl(id);
    }
}
