# 동시성 문제 학습

### 학습목표 
- ___동시성 문제___ 가 발생할 수 있는 대표적인 예시인 재고관리 주제를 통해 동시성 문제를 해결하는 방법을 학습합니다.

### 학습내용
1. **Java Synchronized**: java가 지원하는 synchronized 기능을 통해 간단하게 동시성 문제를 해결해보고 해당 방식의 문제점을 파악합니다.
2. **DataBase Lock**: java의 synchronized 기능의 한계점을 파악하고, 이를 해결하고자 데이터베이스가 지원하는 Lock 기능을 활용하여 동시성 문제를 해결합니다.
   1. **Pessimistic Lock**: 실제로 Data에 Lock을 걸어 정합성을 맞추는 방법입니다. 가장 안전하고 엄격한 방식이지만, Lock이 해제될때까지 다른 트랜잭션에서 데이터에 접근할 수 없기 때문에 DeadLock 위험이 존재합니다.
   2. **Optimistic Lock**: 실제로 Lock을 이용하지 않고 버전을 이용함으로써 정합성을 맞추는 방법입니다. 데이터를 읽은 후 작업을 수행할 때 읽은 데이터의 버전이 최신 버전인지 확인하며 작업을 수행합니다. 읽은 데이터의 버전에서 수정사항이 발생했을 경우 어플리케이션에서 다시 읽은후 작업을 수행해야 하기에 시간이 오래걸릴 수 있습니다.
   3. **Named Lock**: 이름을 가진 Metadata Locking 방식입니다. 이름을 가진 Lock을 획득한 후 Lock을 해제할 때 까지 다른 세션은 이 Lock을 획득할 수 없도록 합니다. 트랜잭션이 종료될 때 Lock이 자동으로 해제되지 않기에 별도의 명령어로 해제해주거나 선점시간이 끝날때 까지 기다려야합니다.
   4. **Named Lock**: 이름을 가진 Metadata Locking 방식입니다. 이름을 가진 Lock을 획득한 후 Lock을 해제할 때 까지 다른 세션은 이 Lock을 획득할 수 없도록 합니다. 트랜잭션이 종료될 때 Lock이 자동으로 해제되지 않기에 별도의 명령어로 해제해주거나 선점시간이 끝날때 까지 기다려야합니다.
3. Redis를 활용한 분산 락: 데이터베이스가 지원하는 기능 이외에 라이브러리로 동시성 문제를 해결합니다.
   1. Lettuce: setnx(set if not exist) 명령어를 활용하여 분산락을 구현합니다. Spin Lock 방식을 사용합니다.
   2. Redisson: Redis의 pub-sub 구조를 기반으로 Lock을 구현합니다.
> 참조
> 
> https://dev.mysql.com/doc/refman/8.0/en
>
> https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html
> 
> https://dev.mysql.com/doc/refman/8.0/en/metadata-locking.html