package org.apache.ignite.ci.tcmodel.hist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.ignite.ci.IgniteTeamcityHelper;
import org.apache.ignite.ci.tcmodel.result.AbstractRef;

/**
 * Actual result of build execution from build history,
 * short version involved in snapshot dependencies and into build history list
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildRef extends AbstractRef {
    @XmlAttribute private Integer id;

    @XmlAttribute public String buildTypeId;

    @XmlAttribute public String branchName;

    @XmlAttribute public String status;

    @XmlAttribute private String state;

    @XmlAttribute public Integer number;

    @XmlAttribute public Boolean defaultBranch;

    public Integer getId() {
        return id;
    }

    public boolean isNotCancelled(IgniteTeamcityHelper helper) {
        return !hasUnknownStatus();
    }

    private boolean hasUnknownStatus() {
        return "UNKNOWN".equals(status);
    }

    public String suiteId() {
        return buildTypeId;
    }
}
