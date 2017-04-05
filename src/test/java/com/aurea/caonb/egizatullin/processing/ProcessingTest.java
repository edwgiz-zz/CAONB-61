package com.aurea.caonb.egizatullin.processing;

import static ch.qos.logback.classic.Level.ERROR;
import static com.aurea.caonb.egizatullin.data.RepositoryState.COMPLETED;
import static com.aurea.caonb.egizatullin.data.RepositoryState.FAILED;
import static com.aurea.caonb.egizatullin.data.RepositoryState.PROCESSING;
import static com.aurea.caonb.egizatullin.test.utils.logback.LogTestUtils.verifyLoggingEvent;
import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_FIELD;
import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_METHOD;
import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_PARAMETER;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.aurea.caonb.egizatullin.data.InspectionDao;
import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.data.RepositoryUniqueKey;
import com.aurea.caonb.egizatullin.test.utils.logback.AbstractLoggingEventListener;
import com.aurea.caonb.egizatullin.und.UndService;
import com.aurea.caonb.egizatullin.und.commons.CodeInspectionType;
import com.aurea.caonb.egizatullin.und.commons.ICodeInspectionCallback;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class ProcessingTest {


    @Test
    public void test() throws Exception {
        GithubService gs = mock(GithubService.class);
        RepositoryDao rd = mock(RepositoryDao.class);
        InspectionDao id = mock(InspectionDao.class);
        UndService us = mock(UndService.class);
        Repository r = new Repository(7,
            "edwgiz",
            "CAONB-61",
            "master",
            "0ece1608f4f2c7db1bc9f0562b0fd744324a3395",
            emptyList());

        try (AbstractLoggingEventListener log = spy(AbstractLoggingEventListener.class)) {
            test(gs, rd, id, us, r, log);
            reset(gs, rd, id, us, log);
            testFail(gs, rd, id, us, r, log);
        }
    }

    private void test(GithubService gs, RepositoryDao rd, InspectionDao id, UndService us,
        Repository r, AbstractLoggingEventListener log) {

        InOrder o = inOrder(gs, rd, id, us, log);
        Path projectDir = Paths.get("testProjectDir");
        doReturn(projectDir)
            .when(gs).download(r.owner, r.repo, r.commitHash);

        Processing p = new Processing(gs, rd, id, us, r);
        p.run();
        RepositoryUniqueKey ruk = new RepositoryUniqueKey(r.owner, r.repo, r.commitHash);
        o.verify(rd, times(1))
            .changeState(eq(ruk), eq(PROCESSING), isNull());
        o.verify(gs, times(1)).download(r.owner, r.repo, r.commitHash);
        o.verify(us, times(1)).buildDatabase(projectDir);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<ICodeInspectionCallback>> codeInspectionsCaptor =
            ArgumentCaptor.forClass(Collection.class);
        o.verify(us, times(1)).inspectCode(eq(projectDir), codeInspectionsCaptor.capture());
        List<CodeInspectionItem> iis = assertCodeInspectionCollectors(
            codeInspectionsCaptor.getValue(), projectDir);
        o.verify(gs).remove(projectDir);
        o.verify(id).addInspections(r.id, iis);
        o.verify(rd, times(1))
            .changeState(eq(ruk), eq(COMPLETED), isNull());
        o.verifyNoMoreInteractions();
    }

    private List<CodeInspectionItem> assertCodeInspectionCollectors(
        Collection<ICodeInspectionCallback> actual,
        Path projectDir) {
        assertEquals(3, actual.size());
        Iterator<ICodeInspectionCallback> it = actual.iterator();
        List<CodeInspectionItem> iis = assertCodeInspectionCollector(
            (CodeInspectionCollector)it.next(), projectDir, UNUSED_METHOD);
        assertSame(iis,  assertCodeInspectionCollector(
            (CodeInspectionCollector)it.next(), projectDir, UNUSED_FIELD));
        assertSame(iis,  assertCodeInspectionCollector(
            (CodeInspectionCollector)it.next(), projectDir, UNUSED_PARAMETER));
        return iis;
    }

    private List<CodeInspectionItem> assertCodeInspectionCollector(
        CodeInspectionCollector cic,
        Path projectDir, CodeInspectionType cit) {
        assertEquals(cic.getCodeInspectionType(), cit);
        assertEquals(projectDir.getNameCount(), cic.getProjectPathDepth());
        return cic.getInspectionItems();
    }

    private void testFail(GithubService gs, RepositoryDao rd, InspectionDao id, UndService us,
        Repository r, AbstractLoggingEventListener log) {

        InOrder o = inOrder(gs, rd, id, us, log);
        doThrow(new RuntimeException("Test github exception"))
            .when(gs).download(r.owner, r.repo, r.commitHash);

        Processing p = new Processing(gs, rd, id, us, r);
        p.run();
        RepositoryUniqueKey ruk = new RepositoryUniqueKey(r.owner, r.repo, r.commitHash);
        o.verify(rd, times(1))
            .changeState(eq(ruk), eq(PROCESSING), isNull());
        o.verify(gs, times(1)).download(r.owner, r.repo, r.commitHash);
        verifyLoggingEvent(o, log, ERROR, RuntimeException.class, "Test github exception");
        o.verify(rd, times(1))
            .changeState(eq(ruk), eq(FAILED), eq("Test github exception"));
        o.verifyNoMoreInteractions();
    }
}