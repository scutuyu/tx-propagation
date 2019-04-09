package com.tuyu.service.impl;

import com.tuyu.dao.TestMapper;
import com.tuyu.service.BService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tuyu
 * @date 4/9/19
 * Talk is cheap, show me the code.
 */
@Slf4j
@Service
public class BServiceImpl implements BService {

    @Autowired
    private TestMapper testMapper;

    @Override
    @Transactional(
//            propagation = Propagation.REQUIRED,
//            propagation = Propagation.SUPPORTS,
//            propagation = Propagation.MANDATORY,
//            propagation = Propagation.REQUIRES_NEW,
//            propagation = Propagation.NOT_SUPPORTED,
            propagation = Propagation.NEVER,
//            propagation = Propagation.NESTED,
            isolation = Isolation.DEFAULT,
            rollbackFor = Exception.class)
    public void b() {
        log.info("start b");
        testMapper.b();
        int i = 12 / 0;
        log.info("end b");
    }
}
