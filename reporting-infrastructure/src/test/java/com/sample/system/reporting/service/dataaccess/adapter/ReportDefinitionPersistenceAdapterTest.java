package com.sample.system.reporting.service.dataaccess.adapter;

import com.sample.system.reporting.service.dataaccess.entity.command.ReportDefinitionEntity;
import com.sample.system.reporting.service.dataaccess.mapper.ReportDefinitionDataAccessMapperImpl;
import com.sample.system.reporting.service.dataaccess.repository.ReportDefinitionRepository;
import com.sample.system.reporting.service.domain.model.ReportDefinitionSearchCriteria;
import com.sample.system.reporting.service.domain.model.entity.ReportDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportDefinitionPersistenceAdapterTest {

    private final ReportDefinitionRepository repository = mock(ReportDefinitionRepository.class);
    private final ReportDefinitionPersistenceAdapter adapter =
            new ReportDefinitionPersistenceAdapter(repository, new ReportDefinitionDataAccessMapperImpl());

    private ReportDefinitionEntity entity(long id, String code, boolean active) {
        ReportDefinitionEntity e = new ReportDefinitionEntity();
        e.setId(id);
        e.setReportCode(code);
        e.setReportName(code + " name");
        e.setActive(active);
        return e;
    }

    @Test
    void findByIdReturnsNullWhenMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertNull(adapter.findById(1L));
    }

    @Test
    void findByReportCodeMapsEntityToDomain() {
        when(repository.findByReportCode("LOAN")).thenReturn(Optional.of(entity(5, "LOAN", true)));
        ReportDefinition found = adapter.findByReportCode("LOAN");
        assertEquals(5L, found.getId().getValue());
        assertEquals("LOAN", found.getReportCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchDefaultsToFirstPageOfTenAndKeepsTotal() {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity(1, "A", true)), org.springframework.data.domain.PageRequest.of(0, 10), 42));

        Page<ReportDefinition> page = adapter.search(new ReportDefinitionSearchCriteria());

        assertEquals(1, page.getContent().size());
        assertEquals(42, page.getTotalElements());
        var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageable.capture());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals(10, pageable.getValue().getPageSize());
    }

    @Test
    void updateActiveTogglesFlagAndReturnsNullForUnknownId() {
        ReportDefinitionEntity stored = entity(7, "X", true);
        when(repository.findById(7L)).thenReturn(Optional.of(stored));
        when(repository.save(stored)).thenReturn(stored);
        when(repository.findById(8L)).thenReturn(Optional.empty());

        ReportDefinition updated = adapter.updateActive(7L, false);

        assertFalse(updated.getActive());
        assertNull(adapter.updateActive(8L, false));
    }
}
