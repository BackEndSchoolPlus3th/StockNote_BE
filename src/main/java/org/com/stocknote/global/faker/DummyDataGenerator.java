package org.com.stocknote.global.faker;//package org.com.stocknote.global.faker;
//
//import com.github.javafaker.Faker;
//import lombok.RequiredArgsConstructor;
//import org.com.stocknote.domain.comment.entity.Comment;
//import org.com.stocknote.domain.comment.repository.CommentRepository;
//import org.com.stocknote.domain.member.entity.Member;
//import org.com.stocknote.domain.member.entity.Role;
//import org.com.stocknote.domain.member.repository.MemberRepository;
//import org.com.stocknote.domain.portfolio.note.entity.Note;
//import org.com.stocknote.domain.portfolio.note.repository.NoteRepository;
//import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
//import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
//import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
//import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
//import org.com.stocknote.domain.post.entity.Post;
//import org.com.stocknote.domain.post.entity.PostCategory;
//import org.com.stocknote.domain.post.repository.PostRepository;
//import org.com.stocknote.domain.stock.entity.Stock;
//import org.com.stocknote.domain.stock.repository.StockRepository;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Configuration
//@RequiredArgsConstructor
//public class DummyDataGenerator {
//    // 더미데이터 부하테스트 -> 개발 환경에서만 사용
//    //1. 유저 데이터 100명
//    private final MemberRepository memberRepository;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//    private final StockRepository stockRepository;
//    private final PortfolioRepository portfolioRepository;
//    private final NoteRepository noteRepository;
//    private final PfStockRepository pfStockRepository;
//    private static final Faker faker = new Faker(Locale.KOREAN);
//    private static final Random random = new Random();
//
//    private static final int BATCH_SIZE = 1000;
//
//    @Transactional
//    public void initializeDummyData() {
//        List<Member> dummyMembers = generateDummyMembers(500);
//        System.out.println(dummyMembers.size() + "명의 더미 회원 생성 완료");
//
//        generateDummyPosts(dummyMembers, 60000);
//        System.out.println("100000개의 더미 게시글 생성 완료");
//
//        generateDummyComments(dummyMembers, 5000);
//        System.out.println("50000개의 더미댓글 생성 완료");
//        generateDummyPortfolioData();
//        System.out.println("주식데이터 생성 완료");
//    }

//    @Transactional
//    public void generateDummyPortfolioData() {
//        List<Member> members = memberRepository.findAll();
//        List<Stock> stocks = stockRepository.findAll();
//
//        generateDummyPortfolios(members, stocks);
//        System.out.println("포트폴리오 더미 데이터 생성 완료");
//    }

//    private List<Stock> createDefaultStocks() {
//        List<Stock> defaultStocks = new ArrayList<>();
//        defaultStocks.add(Stock.builder().name("삼성전자").code("005930").build());
//        defaultStocks.add(Stock.builder().name("카카오").code("035720").build());
//        defaultStocks.add(Stock.builder().name("네이버").code("035420").build());
//
//        return stockRepository.saveAll(defaultStocks);
//    }

//    @Transactional
//    public List<Member> generateDummyMembers(int count) {
//        List<Member> members = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            members.add(Member.builder()
//                    .name(faker.name().fullName())
//                    .email(faker.internet().emailAddress())
//                    .role(Role.USER)
//                    .build());
//        }
//        return memberRepository.saveAll(members);
//    }
//    @Transactional
//    public void generateDummyPosts(List<Member> members, int count) {
//        for (int i = 0; i < count; i += BATCH_SIZE) {
//            List<Post> batch = new ArrayList<>();
//            for (int j = 0; j < BATCH_SIZE && i + j < count; j++) {
//                Member randomMember = members.get(random.nextInt(members.size()));
//                PostCategory randomCategory = PostCategory.values()[random.nextInt(PostCategory.values().length)];
//                Post post = Post.builder()
//                        .title(faker.book().title())
//                        .body(faker.lorem().paragraph())
//                        .member(randomMember)
//                        .category(randomCategory)
//                        .build();
//                batch.add(post);
//            }
//            postRepository.saveAll(batch);
//        }
//    }
//    @Transactional
//    public void generateDummyComments(List<Member> members, int totalComments) {
//        List<Post> posts = postRepository.findAll();
//        List<Comment> allComments = new ArrayList<>();
//        int createdComments = 0;
//
//        while (createdComments < totalComments) {
//            Post randomPost = posts.get(random.nextInt(posts.size()));
//            Member randomMember = members.get(random.nextInt(members.size()));
//            Comment comment = new Comment(randomPost, faker.lorem().sentence(), randomMember);
//            allComments.add(comment);
//            createdComments++;
//
//            if (allComments.size() >= BATCH_SIZE || createdComments == totalComments) {
//                commentRepository.saveAll(allComments);
//                allComments.clear();
//            }
//        }
//        System.out.println(createdComments + "개의 더미 댓글 생성 완료");
//    }
//    @Transactional
//    public void generateDummyPortfolios(List<Member> members, List<Stock> stocks) {
//        for (Member member : members) {
//            // 각 회원당 1~3개의 포트폴리오 생성
//            int portfolioCount = random.nextInt(3) + 1;
//
//            for (int i = 0; i < portfolioCount; i++) {
//                Portfolio portfolio = Portfolio.builder()
//                        .name(faker.funnyName().name() + "의 포트폴리오")
//                        .description(faker.lorem().sentence())
//                        .totalAsset(random.nextInt(100000000) + 10000000) // 1천만원 ~ 1억원
//                        .cash(random.nextInt(50000000)) // 0 ~ 5천만원
//                        .member(member)
//                        .build();
//
//                Portfolio savedPortfolio = portfolioRepository.save(portfolio);
//
//                // 각 포트폴리오당 3~8개의 PfStock 생성
//                generateDummyPfStocks(savedPortfolio, stocks);
//                generateDummyNotes(savedPortfolio, stocks, member);
//            }
//        }
//    }
//    @Transactional
//    public void generateDummyPfStocks(Portfolio portfolio, List<Stock> stocks) {
//        int stockCount = random.nextInt(6) + 3; // 3~8개
//        List<PfStock> pfStocks = new ArrayList<>();
//
//        // 이미 선택된 주식을 추적하기 위한 Set
//        Set<Stock> usedStocks = new HashSet<>();
//
//        for (int i = 0; i < stockCount && usedStocks.size() < stocks.size(); i++) {
//            Stock randomStock;
//            do {
//                randomStock = stocks.get(random.nextInt(stocks.size()));
//            } while (usedStocks.contains(randomStock));  // 이미 사용된 주식이면 다시 선택
//
//            usedStocks.add(randomStock);  // 선택된 주식 기록
//
//            int stockPrice = (random.nextInt(200) + 1) * 1000;
//            int count = random.nextInt(100) + 1;
//
//            PfStock pfStock = PfStock.builder()
//                    .pfstockCount(count)
//                    .pfstockPrice(stockPrice)
//                    .pfstockTotalPrice(stockPrice * count)
//                    .currentPrice(stockPrice + (random.nextInt(20000) - 10000))
//                    .stock(randomStock)
//                    .portfolio(portfolio)
//                    .build();
//
//            pfStocks.add(pfStock);
//        }
//        pfStockRepository.saveAll(pfStocks);
//    }
//    @Transactional
//    public void generateDummyNotes(Portfolio portfolio, List<Stock> stocks, Member member) {
//        int noteCount = random.nextInt(10) + 5; // 5~15개의 노트
//        List<Note> notes = new ArrayList<>();
//
//        String[] types = {"BUY", "SELL"};
//
//        for (int i = 0; i < noteCount; i++) {
//            Stock randomStock = stocks.get(random.nextInt(stocks.size()));
//            String type = types[random.nextInt(types.length)];
//            int price = (random.nextInt(200) + 1) * 1000;
//            int amount = random.nextInt(50) + 1;
//
//            Note note = Note.builder()
//                    .type(type)
//                    .stock(randomStock)
//                    .amount(amount)
//                    .price(price)
//                    .portfolio(portfolio)
//                    .member(member)
//                    .build();
//
//            notes.add(note);
//        }
//        noteRepository.saveAll(notes);
//    }
//
//}
