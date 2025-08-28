package com.example.iam.infrastructure.jpa;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@DataJpaTest
@TestConstructor(autowireMode = ALL)
abstract class BaseJpaSliceTest {}
