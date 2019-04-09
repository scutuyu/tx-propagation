package com.tuyu.service.impl;

import com.tuyu.dao.TestMapper;
import com.tuyu.service.AService;
import com.tuyu.service.BService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AServiceImpl implements AService {

    @Autowired
    private BService bService;

    @Autowired
    private TestMapper testMapper;

    @Override
    @Transactional(
//            propagation = Propagation.REQUIRED,
//            propagation = Propagation.SUPPORTS,
//            propagation = Propagation.MANDATORY,
            propagation = Propagation.REQUIRES_NEW,
//            propagation = Propagation.NOT_SUPPORTED,
//            propagation = Propagation.NEVER,
//            propagation = Propagation.NESTED,
            isolation = Isolation.DEFAULT,
            rollbackFor = Exception.class)
    public void a() {
      log.info("start a");
        testMapper.a();
//        callBWithTryCatch();
        callBWithoutTryCatch();
      log.info("end a");
    }

    private void callBWithTryCatch() {
        try {
            bService.b();
        } catch (Exception e) {
            log.error("捕获了b的异常,但是不抛出，只是打印出来", e);
        }
    }

    private void callBWithoutTryCatch() {
        bService.b();
    }
}
