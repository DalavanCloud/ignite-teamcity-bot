package org.apache.ignite.ci.web.rest.model.current;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.ignite.ci.ITcAnalytics;
import org.apache.ignite.ci.ITeamcity;
import org.apache.ignite.ci.analysis.FullChainRunCtx;
import org.apache.ignite.ci.analysis.ITestFailureOccurrences;
import org.apache.ignite.ci.analysis.MultBuildRunCtx;
import org.apache.ignite.ci.util.CollectionUtil;
import org.apache.ignite.internal.util.typedef.T2;

import static org.apache.ignite.ci.util.UrlUtil.escape;
import static org.apache.ignite.ci.web.rest.model.current.SuiteCurrentStatus.branchForLink;
import static org.apache.ignite.ci.web.rest.model.current.SuiteCurrentStatus.createOccurForLogConsumer;
import static org.apache.ignite.ci.web.rest.model.current.SuiteCurrentStatus.createOrrucForLongRun;

/**
 * Represent Run All chain results/ or RunAll+latest re-runs.
 *
 * Persisted as part of cached result. Renaming require background updater migration.
 */
@SuppressWarnings({"WeakerAccess", "PublicField"})
public class ChainAtServerCurrentStatus {
    /** {@link org.apache.ignite.ci.tcmodel.conf.BuildType#name} */
    public String chainName;

    /** Server ID. */
    public final String serverId;

    /** Branch name in teamcity identification. */
    public final String branchName;

    /** Web Href. to suite runs history*/
    public String webToHist = "";

    /** Web Href. to suite particular run */
    public String webToBuild = "";

    /** Suites involved in chain. */
    public List<SuiteCurrentStatus> suites = new ArrayList<>();

    public Integer failedTests;
    /** Count of suites with critical build problems found */
    public Integer failedToFinish;

    /** Duration printable. */
    public String durationPrintable;

    /** top long running suites */
    public List<TestFailure> topLongRunning = new ArrayList<>();

    /** top log data producing tests . */
    public List<TestFailure> logConsumers = new ArrayList<>();

    /** Special flag if chain entry point not found */
    public boolean buildNotFound;

    public ChainAtServerCurrentStatus(String serverId, String branchTc) {
        this.serverId = serverId;
        this.branchName = branchTc;
    }

    public void initFromContext(ITeamcity teamcity,
        FullChainRunCtx ctx,
        @Nullable ITcAnalytics tcAnalytics,
        @Nullable String failRateBranch) {
        failedTests = 0;
        failedToFinish = 0;
        //todo mode with not failed
        Stream<MultBuildRunCtx> stream = ctx.failedChildSuites();

        stream.forEach(
            suite -> {
                final SuiteCurrentStatus suiteCurStatus = new SuiteCurrentStatus();
                suiteCurStatus.initFromContext(teamcity, suite, tcAnalytics, failRateBranch);

                failedTests += suiteCurStatus.failedTests;
                if (suite.hasAnyBuildProblemExceptTestOrSnapshot())
                    failedToFinish++;

                this.suites.add(suiteCurStatus);
            }
        );
        durationPrintable = ctx.getDurationPrintable();
        webToHist = buildWebLink(teamcity, ctx);
        webToBuild = buildWebLinkToBuild(teamcity, ctx);

        Stream<T2<MultBuildRunCtx, ITestFailureOccurrences>> allLongRunning = ctx.suites().stream().flatMap(
            suite -> suite.getTopLongRunning().map(t->new T2<>(suite, t))
        );
        Comparator<T2<MultBuildRunCtx, ITestFailureOccurrences>> durationComp
            = Comparator.comparing((pair) -> pair.get2().getAvgDurationMs());

        CollectionUtil.top(allLongRunning, 3, durationComp).forEach(
            pairCtxAndOccur -> {
                MultBuildRunCtx suite = pairCtxAndOccur.get1();
                ITestFailureOccurrences longRunningOccur = pairCtxAndOccur.get2();

                TestFailure failure = createOrrucForLongRun(teamcity, suite, tcAnalytics, longRunningOccur, failRateBranch);

                failure.testName = "[" + suite.suiteName() + "] " + failure.testName; //may be separate field

                topLongRunning.add(failure);
            }
        );


        Stream<T2<MultBuildRunCtx, Map.Entry<String, Long>>> allLogConsumers = ctx.suites().stream().flatMap(
            suite -> suite.getTopLogConsumers().map(t->new T2<>(suite, t))
        );
        Comparator<T2<MultBuildRunCtx, Map.Entry<String, Long>>> longConsumingComp
            = Comparator.comparing((pair) -> pair.get2().getValue());

        CollectionUtil.top(allLogConsumers, 3, longConsumingComp).forEach(
            pairCtxAndOccur -> {
                MultBuildRunCtx suite = pairCtxAndOccur.get1();
                Map.Entry<String, Long> testLogConsuming = pairCtxAndOccur.get2();

                TestFailure failure = createOccurForLogConsumer(testLogConsuming);

                failure.name = "[" + suite.suiteName() + "] " + failure.name; //todo suite as be separate field

                logConsumers.add(failure);
            }
        );
    }

    private static String buildWebLinkToBuild(ITeamcity teamcity, FullChainRunCtx chain) {
        return teamcity.host() + "viewLog.html?buildId=" + chain.getSuiteBuildId() ;
    }

    private static String buildWebLink(ITeamcity teamcity, FullChainRunCtx suite) {
        final String branch = branchForLink(suite.branchName());
        return teamcity.host() + "viewType.html?buildTypeId=" + suite.suiteId()
            + "&branch=" + escape(branch)
            + "&tab=buildTypeStatusDiv";
    }

    /**
     * @return Server name.
     */
    public String serverName() {
        return serverId;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChainAtServerCurrentStatus status = (ChainAtServerCurrentStatus)o;
        return Objects.equal(chainName, status.chainName) &&
            Objects.equal(serverId, status.serverId) &&
            Objects.equal(branchName, status.branchName) &&
            Objects.equal(webToHist, status.webToHist) &&
            Objects.equal(webToBuild, status.webToBuild) &&
            Objects.equal(suites, status.suites) &&
            Objects.equal(failedTests, status.failedTests) &&
            Objects.equal(failedToFinish, status.failedToFinish) &&
            Objects.equal(durationPrintable, status.durationPrintable) &&
            Objects.equal(logConsumers, status.logConsumers) &&
            Objects.equal(topLongRunning, status.topLongRunning)&&
            Objects.equal(buildNotFound, status.buildNotFound);
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return Objects.hashCode(chainName, serverId, branchName, webToHist, webToBuild, suites,
            failedTests, failedToFinish, durationPrintable,
            logConsumers, topLongRunning, buildNotFound);
    }

    public void setBuildNotFound(boolean buildNotFound) {
        this.buildNotFound = buildNotFound;
    }
}
