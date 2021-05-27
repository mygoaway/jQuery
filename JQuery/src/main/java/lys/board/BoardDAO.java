package lys.board;

//DBConnectionMgr(DB����,����),BoardDTO(�����͸� ��� ����,�Ű�����,��ȯ��)
//���󿡼� ȣ���� �޼��带 �ۼ�
import java.sql.*;//DB����
import java.util.*;//ArrayList,List�� ���

public class BoardDAO {  //MemberDAO
	
	private DBConnectionMgr pool=null;//1.������ ��ü�� ����
	//�������� ���Ǵ� ��ü�� ��������� ����
	private Connection con=null;
    private PreparedStatement pstmt=null;//����ӵ��� ������.
    private ResultSet rs=null;//select ������ ���->�˻�
    private String sql="";//�����ų SQL���� ����
    
    //2.�����ڸ� ���ؼ� ����
    public BoardDAO() {
    	try {
    		pool=DBConnectionMgr.getInstance();
    		System.out.println("BoardDAO�� pool=>"+pool);
    	}catch(Exception e) {
    		System.out.println("DB���� ����=>"+e);//������,��ȣ,��Ʈ��ȣ Ȯ��?
    	}
    }//������
    //1.����¡ ó���� �ϱ����ؼ� �޼��� 2��->�� ���ڵ���� ���ؿ���
    //select count(*) from board
    public int getArticleCount() {  //MemberDAO ->getMemberCount()
    	int x=0;//�ѷ��ڵ���� ����
    	
    	try {
    		con=pool.getConnection();
    		System.out.println("con=>"+con);//�����
    		sql="select count(*) from board";//select count(*) from member;
    		pstmt=con.prepareStatement(sql);
    		rs=pstmt.executeQuery();
    		if(rs.next()) {//�����ִ� ��� �ִٸ�
    			x=rs.getInt(1);//������=rs.get�ڷ���(�ʵ�� �Ǵ� �ε�����ȣ)
    			//�ʵ���� �������� �ʱ⶧���� �ε�����ȣ�� ��� �ȴ�.(�׷��Լ�)
    		}
    	}catch(Exception e) {
    		System.out.println("getArticleCount() ��������=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return x;
    }
    
    //2.�۸�Ϻ��⿡ �ش��ϴ� �޼��� �ʿ�=>ȸ������Ʈ�� ����(������ ����)
    //���������ڸ� �̿�->limit ?,?
    public List getArticles(int start,int end) {//getMemberList(int strat,int end)
    	
    	List articleList=null; //ArrayList articleList=null; ���ڵ� 10���� ����
    	
    	try {
    		con=pool.getConnection();
    		/*
    		 * �׷��ȣ�� ���� �ֽ��� ���� �߽����� �����ϵ�,���࿡ level�� ���� �׷��� �ִ� ���
    		 * step������ ���������� ���ؼ� ���° ���ڵ��ȣ���� �����ؼ� ���°���ڵ���� 
    		 * �����϶�
    		 * start=>���ڵ��� ���۹�ȣ
    		 * end=>���ڵ��� ����ȣ(X) �ҷ��� ���ڵ��� ����
    		 */
    		sql="select * from board order by ref desc,re_step asc limit ?,?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, start-1);//mysql�� ���ڵ������ ���������� 0���� ����
    		pstmt.setInt(2, end);
    		rs=pstmt.executeQuery();
    		//�۸�Ϻ���=>ȸ������Ʈ ����
    		if(rs.next()) {//���ڵ尡 �ּ� ���� 1���̻� �����Ѵٸ�
    			articleList=new ArrayList(end);
    			//10=>end�� ������ŭ �����͸� ������ ������  �����϶�.
    			//���������� ����(�����ױ�)
    			do {
    				BoardDTO article=makeArticleFromResult();
    			   /*
    			   BoardDTO article=new BoardDTO();//MemberDTO mem=new MemberDTO()
    			   article.setNum(rs.getInt("num"));
    			   article.setWriter(rs.getString("writer"));
    			   article.setEmail(rs.getString("email"));
    			   article.setSubject(rs.getString("subject"));
    			   article.setPasswd(rs.getString("passwd"));
    			   
    			   article.setReg_date(rs.getTimestamp("reg_date"));//�ۼ���¥(now())
    			   article.setReadcount(rs.getInt("readcount"));//��ȸ��
    			   article.setRef(rs.getInt("ref"));//�׷��ȣ->�űԱ۰� �亯�۹����ִ� ����
    			   article.setRe_step(rs.getInt("re_step"));//�亯���� ������ ����(0,1,2 ��������)
    			   article.setRe_level(rs.getInt("re_level"));//�鿩����(�亯�� ����)
    			   
    			   article.setContent(rs.getString("content"));
    			   article.setIp(rs.getString("ip")); */
    			   //�߰�
    			   articleList.add(article);//������ �Ⱦ��� null�� ��
    			}while(rs.next());
    		}
    	}catch(Exception e) {
    		System.out.println("getArticles() �޼��� ��������=>"+e);
    	}finally {
    	    pool.freeConnection(con, pstmt,rs);
    	}
    	return articleList;//list.jsp���� ���
    }
    //3.�Խ����� �۾��� �� �亯�� ����
    //insert into board values(?,?,,,,)
    public void insertArticle(BoardDTO article) {//~ (MemberDTO mem){
    	
    	//1.article=>�űԱ� ���� �亯������ Ȯ��
    	int num=article.getNum();//0(�űԱ�) 0�̾ƴ� ���(�亯��)
    	int ref=article.getRef();
    	int re_step=article.getRe_step();
    	int re_level=article.getRe_level();
    	//���̺� �Է��� �Խù���ȣ�� ������ ����
    	int number=0;
    	System.out.println("insertArticle()�� ���� num=>"+num);
    	System.out.println("ref=>"+ref+",re_step=>"+
    	                               re_step+",re_level=>"+re_level);
    	
    	try {
    		con=pool.getConnection();
    		//�����͸� �־��ٶ� �ʿ���ϴ� �Խù���ȣ�� �ʿ�
    		sql="select max(num) from board";
    		pstmt=con.prepareStatement(sql);
    		rs=pstmt.executeQuery();
    		//������ ����üũ
    		if(rs.next()) {//�����ִ� ����� �ִٸ� ->rs.last()->rs.getRow();
    			number=rs.getInt(1)+1;
    		}else {//���� ���̺� �����Ͱ� �Ѱ��� ���� ��� (0)
    			number=1;
    		}
    		//�亯���̶��
    		if(num!=0) {//����̸鼭 1�̻�
    			//���� �׷��ȣ�� ������ �����鼭 ��(�߰��� ������� �Խù�)���� ū �Խù��� ã�Ƽ�
    			//�� step���� �������Ѷ�
    			sql="update board set re_step=re_step+1 where ref=? and re_step > ?";
    			pstmt=con.prepareStatement(sql);
    			pstmt.setInt(1, ref);
    			pstmt.setInt(2, re_step);
    			int update=pstmt.executeUpdate();
    			System.out.println("��ۼ�������(update)=>"+update);//1 ����
    			//�亯��
    			re_step=re_step+1;
    			re_level=re_level+1;
    		}else {//�űԱ��̶�� num=0(writeForm.jsp)
    			ref=number;//1,2,3,4,5
    			re_step=0;
    			re_level=0;
    		}
    		//12��->num,reg_date,readcount(����)=>default
    		//�ۼ���¥->sysdate,now()�� ����ϸ� �ȴ�.
    		sql="insert into board(writer,email,subject,passwd,reg_date,";
    		sql+=" ref,re_step,re_level,content,ip)values(?,?,?,?,?,?,?,?,?,?)";
    		//~ (?,?,?,?,now(),?,?,?,?,?)
    		pstmt=con.prepareStatement(sql);
    		pstmt.setString(1, article.getWriter());//�������� Setter�� ����� ����
    		pstmt.setString(2, article.getEmail());
    		pstmt.setString(3, article.getSubject());
    		pstmt.setString(4, article.getPasswd());
    		pstmt.setTimestamp(5, article.getReg_date());//��ſ� now()�� �̿��Ҽ��� �ִ�.
    		//---------------ref,re_step,re_level�� ���� ����� ������ �� ���¿��� ����
    		pstmt.setInt(6, ref);//pstmt.setInt(6, article.getRef());
    		pstmt.setInt(7, re_step);
    		pstmt.setInt(8, re_level);
    		//--------------------------------------------------------------------
    		pstmt.setString(9, article.getContent());
    		pstmt.setString(10, article.getIp());//request.getRemoteAddr()
    		int insert=pstmt.executeUpdate();
    		System.out.println("�Խ����� �۾��� ��������(insert)=>"+insert);
    	}catch(Exception e) {
    		System.out.println("insertArticle() �޼��� ��������=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    }
    //------�ۻ󼼺���--------------------------------------------------------------------
    /*
     * <a href="content.jsp?num=<%=article.getNum()%>&pageNum=<%=currentPage%>">
           <%=article.getSubject() %></a> 
     */
    //����) select * from board where num=3
    //����) update board set readcount=readcount+1 where num=3
    public BoardDTO getArticle(int num) {
    	
    	BoardDTO article=null;//ArrayList articleList=null;
    	
    	try {
    		con=pool.getConnection();
    		/*
    		 1.��ȸ�� ����
    		 2.�����͸� ���
    		 */
    		sql="update board set readcount=readcount+1 where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);//mysql�� ���ڵ������ ���������� 0���� ����
    		int update=pstmt.executeUpdate();
    		System.out.println("��ȸ�� ��������(update)=>"+update);
    		
    		sql="select * from board where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);
    		rs=pstmt.executeQuery();
    		
    		//������ ���ڵ带 ã�Ҵٸ�
    		if(rs.next()) {//���ڵ尡 �ּ� ���� 1���̻� �����Ѵٸ�
    			
    			   article=makeArticleFromResult();
    			   /*
    			   article=new BoardDTO();//MemberDTO mem=new MemberDTO()
    			   article.setNum(rs.getInt("num"));
    			   article.setWriter(rs.getString("writer"));
    			   article.setEmail(rs.getString("email"));
    			   article.setSubject(rs.getString("subject"));
    			   article.setPasswd(rs.getString("passwd"));
    			   
    			   article.setReg_date(rs.getTimestamp("reg_date"));//�ۼ���¥(now())
    			   article.setReadcount(rs.getInt("readcount"));//��ȸ��
    			   article.setRef(rs.getInt("ref"));//�׷��ȣ->�űԱ۰� �亯�۹����ִ� ����
    			   article.setRe_step(rs.getInt("re_step"));//�亯���� ������ ����(0,1,2 ��������)
    			   article.setRe_level(rs.getInt("re_level"));//�鿩����(�亯�� ����)
    			   
    			   article.setContent(rs.getString("content"));
    			   article.setIp(rs.getString("ip"));
    			   */
    		}
    	}catch(Exception e) {
    		System.out.println("getArticle() �޼��� ��������=>"+e);
    	}finally {
    	    pool.freeConnection(con, pstmt,rs);
    	}
    	return article;//list.jsp���� ���
    }
    
    //--------�ߺ��� ���ڵ� �Ѱ��� ���� �� �ִ� ���� �޼��带 �ۼ�------
    private BoardDTO makeArticleFromResult() throws Exception {
    	
    	   BoardDTO article=new BoardDTO();//MemberDTO mem=new MemberDTO()
		   article.setNum(rs.getInt("num"));
		   article.setWriter(rs.getString("writer"));
		   article.setEmail(rs.getString("email"));
		   article.setSubject(rs.getString("subject"));
		   article.setPasswd(rs.getString("passwd"));
		   
		   article.setReg_date(rs.getTimestamp("reg_date"));//�ۼ���¥(now())
		   article.setReadcount(rs.getInt("readcount"));//��ȸ��
		   article.setRef(rs.getInt("ref"));//�׷��ȣ->�űԱ۰� �亯�۹����ִ� ����
		   article.setRe_step(rs.getInt("re_step"));//�亯���� ������ ����(0,1,2 ��������)
		   article.setRe_level(rs.getInt("re_level"));//�鿩����(�亯�� ����)
		   
		   article.setContent(rs.getString("content"));
		   article.setIp(rs.getString("ip"));
		   return article;
    }
    
    //�ۼ���
    //1) ������ �����͸� ã�Ƽ� ȭ�鿡 ��½����ִ� ����->updateForm.jsp (writeForm.jsp)
    //select * from board where num=3
    public BoardDTO updateGetArticle(int num) {
         BoardDTO article=null;//ArrayList articleList=null;
    	try {
    		con=pool.getConnection();
    		
    		sql="select * from board where num=?";//1~10
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1, num);
    		rs=pstmt.executeQuery();
    		
    		//������ ���ڵ带 ã�Ҵٸ�
    		if(rs.next()) {//���ڵ尡 �ּ� ���� 1���̻� �����Ѵٸ�
    			   article=makeArticleFromResult(); 
    		}
    	}catch(Exception e) {
    		System.out.println("updateGetArticle() �޼��� ��������=>"+e);
    	}finally {
    	    pool.freeConnection(con, pstmt,rs);
    	}
    	return article;//list.jsp���� ���
    }
    
    //2) ���������ִ� �޼��� �ۼ�->updatePro.jsp<->writePro.jsp
    public int updateArticle(BoardDTO article) {//insertArticle
    	
    	String dbpasswd=null;//db���� ã�� ��ȣ�� ����
    	int x=-1;//�Խù��� ������������
    	
    	try {
    		con=pool.getConnection();
    		sql="select passwd from board where num=?";
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1,article.getNum());//index~�ε����ο��ؾ� �Ѵ�.
    		rs=pstmt.executeQuery();
    		//������ ����üũ
    		if(rs.next()) {//�����ִ� ����� �ִٸ� ->rs.last()->rs.getRow();
    		   dbpasswd=rs.getString("passwd");
    		   System.out.println("dbpasswd=>"+dbpasswd);//���߿� �������� �κ�
    		   
    		   if(dbpasswd.contentEquals(article.getPasswd())) {
		    		sql="update board set writer=?,email=?,subject=?,passwd=?,";
		    		sql+=" content=?  where num=?";
		    		//
		    		pstmt=con.prepareStatement(sql);
		    		pstmt.setString(1, article.getWriter());//�������� Setter�� ����� ����
		    		pstmt.setString(2, article.getEmail());
		    		pstmt.setString(3, article.getSubject());
		    		pstmt.setString(4, article.getPasswd());
		    		pstmt.setString(5, article.getContent());
		    		pstmt.setInt(6, article.getNum());
		    		
		    		int update=pstmt.executeUpdate();
		    		System.out.println("�Խ����� �ۼ��� ��������(update)=>"+update);
		    		x=1;//�������� ǥ��
    		   }else {//��ȣ�� Ʋ�����
    			   x=0;//��������
    		   }
    		}else {//if(rs.next()==false)
    			x=-1;//ã�� �����Ͱ� ���� ���(��������)
    		}
    	}catch(Exception e) {
    		System.out.println("updateArticle() �޼��� ��������=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return x;
    }
    
    //3)���������ִ� �޼��� �ۼ�=>���������ִ� �޼��� ����
    //delete from board where num=3
    public int deleteArticle(int num,String passwd) {
    	
    	String dbpasswd=null;//db���� ã�� ��ȣ�� ����
    	int x=-1;//�Խù��� ������������
    	
    	try {
    		con=pool.getConnection();
    		sql="select passwd from board where num=?";
    		pstmt=con.prepareStatement(sql);
    		pstmt.setInt(1,num);
    		rs=pstmt.executeQuery();
    		//������ ����üũ
    		if(rs.next()) {//�����ִ� ����� �ִٸ� ->rs.last()->rs.getRow();
    		   dbpasswd=rs.getString("passwd");
    		   System.out.println("dbpasswd=>"+dbpasswd);//���߿� �������� �κ�
    		   
    		   if(dbpasswd.contentEquals(passwd)) {
		    		sql="delete from board where num=?";
		    		pstmt=con.prepareStatement(sql);
		    		pstmt.setInt(1, num);
		    		int delete=pstmt.executeUpdate();
		    		System.out.println("�Խ����� �ۻ��� ��������(delete)=>"+delete);
		    		x=1;//�������� ǥ��
    		   }else {//��ȣ�� Ʋ�����
    			   x=0;//��������
    		   }
    		}//if(rs.next())
    	}catch(Exception e) {
    		System.out.println("deleteArticle() �޼��� ��������=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return x;
    }
   //-------------------------------------------------- 
    //실시간 검색어->writer(회원 id대신으로 사용)
    //select writer  from board where writer like '%?%';
    public List<String> getArticleId(String name){
    	List<String> nameList=new ArrayList();
    	try {
    		con=pool.getConnection();
    		sql="select writer  from board where writer like '%"+name+"%'";
    		pstmt=con.prepareStatement(sql);
    		rs=pstmt.executeQuery();
    		while(rs.next()) {
    			String writer=rs.getString("writer");
    			nameList.add(writer);
    		}
    	}catch(Exception e) {
    		System.out.println("getArticleId() 메서드 에러유발=>"+e);
    	}finally {
    		pool.freeConnection(con, pstmt, rs);
    	}
    	return nameList;//autoid.jsp에 리턴
    }
}






