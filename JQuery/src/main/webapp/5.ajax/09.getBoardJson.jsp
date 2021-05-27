<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.sql.*,lys.board.*"%>
[
 <%
    Connection con=null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    DBConnectionMgr pool=null;
    String sql="";
    
    try{
    	pool=DBConnectionMgr.getInstance();
    	con=pool.getConnection();
    	sql="select * from board order by num asc";//게시물번호의 오름차순
    	pstmt=con.prepareStatement(sql);
    	rs=pstmt.executeQuery();
    	while(rs.next()){
    		//[{num:1,writer:'홍길동',,,},{}]
    		int num=rs.getInt("num");
    		String writer=rs.getString("writer");
    		String subject=rs.getString("subject");
    		String content=rs.getString("content");
    		if(rs.getRow() > 1){//rs.getRow()=>DB상의 행의수를 구해주는 메서드
    			out.print(",");      //한개이상의 레코드를 가지고 있다면
    		}%>
         {
          "num":<%=num %>,<br>
          "writer":<%=writer %>,<br>
          "subject":<%=subject %>,<br>
          "content":<%=content %><br>
         }
   <%  		
    	}
    }catch(Exception e){
    	out.println("getBoardJson.jsp에 에러유발=>"+e);
    }finally{
    	pool.freeConnection(con, pstmt, rs);
    }
 %>
]
