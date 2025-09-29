package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.respository.AssemblyMemberRespotiroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class AssemblyMemberService {

    private  AssemblyMemberRespotiroy assemblyMemberRespotiroy;

    public AssemblyMemberService(@Autowired AssemblyMemberRespotiroy assemblyMemberRespotiroy) {
        this.assemblyMemberRespotiroy = assemblyMemberRespotiroy;
    }

    public Page<AssemblyMember> getAssemblyMemberAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return assemblyMemberRespotiroy.findAll(pageable);
    }
}
