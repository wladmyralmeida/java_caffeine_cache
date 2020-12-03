package com.caffeine.example.demo.utils;

import com.caffeine.example.demo.data.DataObject;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MetodosParaDespejarObjetos {

    // O Despejo ocorre quando o limite de tamanho configurado do cache é excedido!!!

    // Existem duas maneiras de obter os objetos: contagem de tamanho no cache ou obtendo seus pesos.
    public static void removeBasedInSize() {

        // o cache se inicia com tamanho = 0;
        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(1)
                .build(k -> DataObject.get("Data for " + k));

        assertEquals(0, cache.estimatedSize());

        // Quando adicionamos um valor, o tamanho = tamanho +1:
        cache.get("A");

        assertEquals(1, cache.estimatedSize());

        // Podemos adicionar o segundo valor ao cache, o que leva à remoção do primeiro valor:
        cache.get("B");

        // aguarda a conclusão a remoção do A, usado devido o assincronismo do método;
        cache.cleanUp();

        assertEquals(1, cache.estimatedSize());


        // **função de pesagem para obter o tamanho do cache
        cache = Caffeine.newBuilder()
                .maximumWeight(10)
                .weigher((k, v) -> 5)
                .build(k -> DataObject.get("Data for " + k));

        // !! Lembrar que os valores são removidos do cache quando o peso é superior a 10.
    }

    // A estratégia baseada no tempo de expiração da entrada têm três tipos
    // 1 - Expira após o acesso - a entrada expira após o período decorrido desde a última leitura ou gravação ocorrer;
    // 2 - Expirar após gravação - a entrada expira após o período decorrido desde a última gravação;
    // 3 - Política personalizada - um tempo de expiração é calculado para cada entrada individualmente pela
    // implementação de expiração
    public static void removeBasedInTime() {

        //expireAfterAccess
        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(k -> DataObject.get("Data for " + k));

        //expireAfterWhite
        cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .weakKeys()
                .weakValues()
                .build(k -> DataObject.get("Data for " + k));

        //customExpire - É necessário implementar a interface Expiry!!!
        cache = Caffeine.newBuilder().expireAfter(new Expiry<String, DataObject>() {
            @Override
            public long expireAfterCreate(
                    String key, DataObject value, long currentTime) {
                return value.getData().length() * 1000;
            }
            @Override
            public long expireAfterUpdate(
                    String key, DataObject value, long currentTime, long currentDuration) {
                return currentDuration;
            }
            @Override
            public long expireAfterRead(
                    String key, DataObject value, long currentTime, long currentDuration) {
                return currentDuration;
            }
        }).build(k -> DataObject.get("Data for " + k));
    }

    // remoção baseada em referência - configurar o cache para coleta de lixo de chaves e / ou valores de cache
    // 1 - WeakRefence para chave e valor
    // 2 - SoftReference apenas valores;
    // -------------------------------------
    // 1 - Permite a coleta de lixo de objetos quando não há nenhuma referência forte ao objeto.
    // 2 - Permite coletar como lixo com base na estratégia global Least-Recentemente-Used da JVM.
    // 2.1 - Doc to WeakHashMap = https://www.baeldung.com/java-weakhashmap
    public static void removeBasedInReference() {

        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .weakKeys()
                .weakValues()
                .build(k -> DataObject.get("Data for " + k));

        cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .softValues()
                .build(k -> DataObject.get("Data for " + k));
    }
}
