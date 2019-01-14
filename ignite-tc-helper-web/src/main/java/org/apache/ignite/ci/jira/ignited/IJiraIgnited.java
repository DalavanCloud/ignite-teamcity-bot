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

import java.util.Set;
import org.apache.ignite.ci.jira.Ticket;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface IJiraIgnited {
    @NotNull public String ticketPrefix();

    @NotNull public String projectName();


    /**
     * @return Jira tickets.
     */
    public Set<Ticket> getTickets();

    /**
     * @param ticketFullName Ticket full name (e.g IGNITE-8331)
     * @return URL which is used as link to Jira comment with specified id.
     */
    public String generateCommentUrl(String ticketFullName, int commentId);

    /**
     * @param id Ticket full ID (e.g IGNITE-8331)
     * @return URL which is used as link to Jira ticket with specified name.
     */
    String generateTicketUrl(String id);

}
