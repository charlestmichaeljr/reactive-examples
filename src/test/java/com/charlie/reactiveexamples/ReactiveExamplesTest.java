package com.charlie.reactiveexamples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExamplesTest {

    Person michael = new Person("Michael","Weston");
    Person fiona = new Person("Fiona", "Glenanne");
    Person sam = new Person ("Sam", "Axe");
    Person jesse = new Person ("Jesse","Porter");



    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void monoTests() throws Exception {
        Mono<Person> personMono = Mono.just(michael);

        Person person = personMono.block();

        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform() {
        Mono<Person>  personMono = Mono.just(fiona);

        PersonCommand personCommand = personMono.map(person -> {
            return new PersonCommand(person);
        }).block();

        log.info(personCommand.sayMyName());
    }

    @Test(expected = NullPointerException.class)
    public void filterTest() {
        Mono<Person>  personMono = Mono.just(sam);
        Person samAxe = personMono.filter(person -> person.getFirstName().equalsIgnoreCase("foo"))
                .block();

        log.info(samAxe.sayMyName());
    }

    @Test
    public void fluxTest() throws Exception {
        Flux<Person> people = Flux.just(michael, sam, fiona,jesse);

        people.subscribe(person -> {
            log.info(person.sayMyName());
        });
    }


    @Test
    public void fluxTestFilter() throws Exception {

        Flux<Person> people = Flux.just(michael, sam, fiona,jesse);

        people.filter(person ->
            person.getFirstName().toLowerCase().contains("s")
        ).subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput() throws Exception {

        Flux<Person> people = Flux.just(michael, sam, fiona,jesse);

        people.delayElements(Duration.ofSeconds(1)).subscribe(person -> log.info(person.sayMyName()));

    }

    @Test
    public void fluxTestDelay() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, sam, fiona,jesse);

        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();


    }

    @Test
    public void fluxTestFilterDelay() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, sam, fiona,jesse);

        people.filter(person -> person.getLastName().toLowerCase().contains("o"))
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }
}
