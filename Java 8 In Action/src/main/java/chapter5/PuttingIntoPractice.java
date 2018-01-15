package chapter5;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * 5.5 付诸实践
 */
public class PuttingIntoPractice {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        // 1 找出2011年所有排序，并按交易额排序（低->高）
        //[{Trader:Brian in Cambridge, year: 2011, value:300}, {Trader:Raoul in Cambridge, year: 2011, value:400}]
        List<Transaction> transactions1 =
                transactions.stream()
                            .filter(t -> t.getYear() == 2011)
                            .sorted(comparing(Transaction::getValue))
                            .collect(toList());
        System.out.println(transactions1);

        // 2 交易员都在哪些不同城市工作过
        // [Cambridge, Milan]
        List<String> transactions2 =
                transactions.stream()
            //                .map(t->t.getTrader().getCity())
                            .map(Transaction::getTrader)
                            .map(Trader::getCity)
                            .distinct()
                            .collect(toList());
        System.out.println(transactions2);

        // 3 查找所有来自 Cambridge 的人员，并按姓名排序
        // [Trader:Alan in Cambridge, Trader:Brian in Cambridge, Trader:Raoul in Cambridge]
        List<Trader> transactions3 =
                transactions.stream()
                            .map(Transaction::getTrader)
                            .filter(t -> "Cambridge".equals(t.getCity()))
                            .distinct()
                            .sorted(comparing(Trader::getName))
                            .collect(toList());
        System.out.println(transactions3);

        // 4 返回所有交易员的姓名字符，并按字母排序
        // AlanBrianMarioRaoul
        String transactions4 =
                transactions.stream()
                            .map(Transaction::getTrader)
                            .map(Trader::getName)
                            .distinct()
                            .sorted()
                            .reduce("", (n1, n2) -> n1+n2);
        System.out.println(transactions4);

        // 5 有没有交易员在 Milan
        // true
        boolean transactions5 =
                transactions.stream()
                            .anyMatch(t -> "Milan".equals(t.getTrader().getCity()));
        System.out.println(transactions5);

        // 6 打印在 Cambridge 的所有交易额
        // 300 1000 400 950
        transactions.stream()
                    .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                    .map(Transaction::getValue)
                    .forEach(i -> System.out.print(i+" "));

        // 7 最高交易额是多少
        // 1000
        int transactions7 =
                transactions.stream()
                            .map(Transaction::getValue)
                            .reduce(0, Integer::max);
        System.out.println("\n"+transactions7);

        // 交易额最小的交易
        // {Trader:Brian in Cambridge, year: 2011, value:300}
        Optional<Transaction> transactions8 =
                transactions.stream()
                            .min(comparing(Transaction::getValue));
        System.out.println(transactions8.get());
    }
}
