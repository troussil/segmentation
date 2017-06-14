import java.util.LinkedList;

public class OptimizedAdjMatrix{

	private int[][] adjMatrix,residualGraph;
	private int source, sink;
	private int nbPixels,height,width;

	public OptimizedAdjMatrix(int[][] binaryImg, int beta, int t1, int t2){
		this.height=binaryImg.length;
		this.width=binaryImg[0].length;
		this.nbPixels=height*width;
		this.source=nbPixels;
		this.sink=nbPixels+1;

		//Adjacency matrix initialization
		adjMatrix=new int[nbPixels+2][];
		for(int i=0;i<nbPixels;i++){
			adjMatrix[i]=new int[10]; //0-3:Left/Right/Above/Below; 4-5:source/sink; 6-9: haut vers bas, gauche vers droite
			for(int j=0;j<10;j++)
				adjMatrix[i][j]=0;
		}
		adjMatrix[nbPixels]=new int[nbPixels];  //The source and the sink have max nbPixels neighbours
		adjMatrix[nbPixels+1]=new int[nbPixels];
		for(int i=0;i<nbPixels;i++){
			adjMatrix[nbPixels][i]=0;
			adjMatrix[nbPixels+1][i]=0;			//Will be useful for the residual graph
		}

		//Construction of the graph
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				int indice=i*width+j;
				int valeur=binaryImg[i][j];
				if(valeur<t1){   //Edge from this pixel to the sink
					adjMatrix[indice][5]=(int)(1*(t1-valeur));
					//System.out.println(adjMatrix[indice][5]);
				} else if(valeur>t2){				  //Edge from the source to this pixel
					adjMatrix[nbPixels][indice]=(int)(1*(valeur-t2));
					//System.out.println(adjMatrix[nbPixels][indice]);
				}
				if(i>0){				//Neighbour above
					adjMatrix[indice][2]=beta;
				}
				if(i<height-1){			//Neighbour below
					adjMatrix[indice][3]=beta;
				}
				if(j>0){				//Neighbour to the left
					adjMatrix[indice][0]=beta;
				}
				if(j<width-1){			//Neighbour to the right
					adjMatrix[indice][1]=beta;
				}
				if(i>0 && j>0){			//Above and left
					adjMatrix[indice][6]=beta;
				}
				if(i>0 && j<width-1){	//Above and right
					adjMatrix[indice][7]=beta;
				}
				if(i<height-1 && j>0){	//Below and left
					adjMatrix[indice][8]=beta;
				}
				if(i<height-1 && j<width-1){	//Below and right
					adjMatrix[indice][9]=beta;
				}
			}
		}
	}

	public int[][] pushRelabelAlgo(){

		initResidualGraph();

		int[] h = new int[nbPixels+2];  //Tableau des hauteurs de chaque noeud
		int[] excess = new int[nbPixels+2];			//Tableau donnant l'excédent de flux par noeud

		for (int i = 0; i < nbPixels; ++i) {	//Initialisation des flots de la source vers ses voisins
		  residualGraph[source][i] -= adjMatrix[source][i];
		  residualGraph[i][4] += adjMatrix[source][i];
		  excess[i] = adjMatrix[source][i];
		  excess[source] -=adjMatrix[source][i];
		  h[i]=0;
		}
		excess[sink]=0;
		h[source]=nbPixels+2;
		h[sink]=0;

		while(true){
			boolean action=push(excess,h);
			if(!action)
				action=relabel(excess,h);
			if(!action)
				break;
		}


		System.out.println("Computing the corresponding cut");
		//Find the cut thanks to the residual graph
		int[] cut=new int[nbPixels+2];
		for(int i=0;i<nbPixels+2;i++)
			cut[i]=0;

		residualGraphExporation(cut,nbPixels+2);

		//Creating the image thanks to the cut
		int[][] img=new int[height][width];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(cut[i*width+j]==1){
					//System.out.println("1");
					img[i][j]=1;
				} else {
					img[i][j]=0;
					//System.out.println("0");
				}
			}
		}
		int flow=0;
		for(int i=0;i<nbPixels;i++)
			flow+=(adjMatrix[source][i]-residualGraph[source][i]);
		//System.out.println("Flot max de "+flow);

		return img;
	}

	private boolean push(int[] excess,int[] h){
		boolean pushDone=false;
		for(int i=0;i<excess.length-2;i++){
			if(i==sink)
				continue;
			if(pushDone)
				break;
			if(excess[i]>0){
				int nbNeighbours=residualGraph[i].length;
				int yNode=i%width;
				int xNode=(i-yNode)/width;
				for (int neighbour=0; neighbour<nbNeighbours; neighbour++)
				{
					if(pushDone)
						break;
					if(residualGraph[i][neighbour] > 0){
						int indiceN=0;
						int indiceFromNeighbour=0;
						if(i==source){
							indiceN=neighbour;
							indiceFromNeighbour=4;
						} else if(i==sink){
							indiceN=neighbour;
							indiceFromNeighbour=5;
						} else {
							switch(neighbour){
								case 0:
									indiceN=(xNode)*width+(yNode-1);
									indiceFromNeighbour=1;
									break;
								case 1:
									indiceN=(xNode)*width+(yNode+1);
									indiceFromNeighbour=0;
									break;
								case 2:
									indiceN=(xNode-1)*width+yNode;
									indiceFromNeighbour=3;
									break;
								case 3:
									indiceN=(xNode+1)*width+yNode;
									indiceFromNeighbour=2;
									break;
								case 4:
									indiceN=nbPixels;
									indiceFromNeighbour=i;
									break;
								case 5:
									indiceN=nbPixels+1;
									indiceFromNeighbour=i;
									break;
								case 6:
									indiceN=(xNode-1)*width+(yNode-1);
									indiceFromNeighbour=9;
									break;
								case 7:
									indiceN=(xNode-1)*width+(yNode+1);
									indiceFromNeighbour=8;
									break;
								case 8:
									indiceN=(xNode+1)*width+(yNode-1);
									indiceFromNeighbour=7;
									break;
								case 9:
									indiceN=(xNode+1)*width+(yNode+1);
									indiceFromNeighbour=6;
									break;
							}
						}
						if(h[indiceN]<h[i]){
							int toPush=Math.min(excess[i],residualGraph[i][neighbour]);
							excess[i]-=toPush;
							excess[indiceN]+=toPush;
							residualGraph[i][neighbour]-=toPush;
							residualGraph[indiceN][indiceFromNeighbour]+=toPush;
							pushDone=true;
							//System.out.println("Push "+toPush+" de "+i+" vers "+indiceN);
						}
					}
				}
			}
		}
		return pushDone;
	}

	private boolean relabel(int[] e,int[] h){
		boolean relabelDone=false;

		for(int i=0;i<e.length-2;i++){
			if(i==sink)
				continue;
			if(e[i]>0){
				int nbNeighbours=residualGraph[i].length;
				int yNode=i%width;
				int xNode=(i-yNode)/width;
				int minH=Integer.MAX_VALUE;
				for (int neighbour=0; neighbour<nbNeighbours; neighbour++)
				{
					if(residualGraph[i][neighbour] > 0){
						relabelDone=true;
						int indiceN=0;
						if(i==source){
							indiceN=neighbour;
						} else if(i==sink){
							indiceN=neighbour;
						} else {
							switch(neighbour){
								case 0:
									indiceN=(xNode)*width+(yNode-1);
									break;
								case 1:
									indiceN=(xNode)*width+(yNode+1);
									break;
								case 2:
									indiceN=(xNode-1)*width+yNode;
									break;
								case 3:
									indiceN=(xNode+1)*width+yNode;
									break;
								case 4:
									indiceN=nbPixels;
									break;
								case 5:
									indiceN=nbPixels+1;
									break;
								case 6:
									indiceN=(xNode-1)*width+(yNode-1);
									break;
								case 7:
									indiceN=(xNode-1)*width+(yNode+1);
									break;
								case 8:
									indiceN=(xNode+1)*width+(yNode-1);
									break;
								case 9:
									indiceN=(xNode+1)*width+(yNode+1);
									break;
							}
						}
						minH=Math.min(minH,h[indiceN]);
					}
				}
				if(relabelDone){
					h[i]=minH+1;
					//System.out.println("Relabel de "+i+ " a "+h[i]);
					return true;
				}
			}
		}
		return false;
	}

	public int[][] minCut(){

		initResidualGraph();
		System.out.println("Computing the max-flow");

		int[] path=new int[nbPixels+2];
		for(int i=0;i<nbPixels+2;i++)
			path[i]=-1;

		//While there is an augmenting path from source to sink, update the residual graph
		while(breadth_First_Search(path,nbPixels+2)){
			//System.out.println("Chemin");
			int pathFlow=Integer.MAX_VALUE;
			//Find the flow of the augmenting path
			int j=path[sink];
			for(int i=sink; i!=source;i=path[i]){
				int edgeWeight=0;
				if(j==source){
					edgeWeight=residualGraph[source][i];
				} else if(i==sink) {
					edgeWeight=residualGraph[j][5];
				} else {              //i=xI*width+yI;
					int yI=i%width;
					int xI=(i-yI)/width;
					int yJ=j%width;
					int xJ=(j-yJ)/width;
					if(yI==yJ+1 && xI==xJ){  	//i=right neighbour of j
						edgeWeight=residualGraph[j][1];
					}else if(yI==yJ-1 && xI==xJ){
						edgeWeight=residualGraph[j][0];
					}else if(xI==xJ+1 && yI==yJ){
						edgeWeight=residualGraph[j][3];
					}else if(xI==xJ-1 && yI==yJ) {
						edgeWeight=residualGraph[j][2];
					} else if(xI==xJ-1 && yI==yJ-1) {
						edgeWeight=residualGraph[j][6];
					} else if(xI==xJ-1 && yI==yJ+1) {
						edgeWeight=residualGraph[j][7];
					} else if(xI==xJ+1 && yI==yJ-1) {
						edgeWeight=residualGraph[j][8];
					} else if(xI==xJ+1 && yI==yJ+1) {
						edgeWeight=residualGraph[j][9];
					}


				}
				//System.out.println("Arc de "+j+" a "+i+" de poids " +edgeWeight);
				pathFlow=Math.min(pathFlow,edgeWeight);
				j=path[j];
			}
			//System.out.println("PathFlow de "+pathFlow);
			//Update the residual graph
			j=path[sink];
			for(int i=sink; i!=source;i=path[i]){
				if(j==source){
					residualGraph[i][4]+=pathFlow;
					residualGraph[source][i]-=pathFlow;
				} else if(i==sink) {
					residualGraph[sink][j]+=pathFlow;
					residualGraph[j][5]-=pathFlow;
				} else {              //i=xI*width+yI;
					int yI=i%width;
					int xI=(i-yI)/width;
					int yJ=j%width;
					int xJ=(j-yJ)/width;

					if(yI>yJ && xI==xJ){  	//i=right neighbour of j
						residualGraph[i][0]+=pathFlow;
						residualGraph[j][1]-=pathFlow;
					}else if(yI<yJ && xI==xJ){
						residualGraph[i][1]+=pathFlow;
						residualGraph[j][0]-=pathFlow;
					}else if(xI>xJ && yI==yJ){
						residualGraph[i][2]+=pathFlow;
						residualGraph[j][3]-=pathFlow;
					}else if(xI<xJ && yI==yJ) {
						residualGraph[i][3]+=pathFlow;
						residualGraph[j][2]-=pathFlow;
					} else if(xI==xJ-1 && yI==yJ-1) {
						residualGraph[i][9]+=pathFlow;
						residualGraph[j][6]-=pathFlow;
					} else if(xI==xJ-1 && yI==yJ+1) {
						residualGraph[i][8]+=pathFlow;
						residualGraph[j][7]-=pathFlow;
					} else if(xI==xJ+1 && yI==yJ-1) {
						residualGraph[i][7]+=pathFlow;
						residualGraph[j][8]-=pathFlow;
					} else if(xI==xJ+1 && yI==yJ+1) {
						residualGraph[i][6]+=pathFlow;
						residualGraph[j][9]-=pathFlow;
					}

				}
				j=path[j];
			}
		}


		System.out.println("Computing the corresponding cut");
		//Find the cut thanks to the residual graph
		int[] cut=new int[nbPixels+2];
		for(int i=0;i<nbPixels+2;i++)
			cut[i]=0;

		residualGraphExporation(cut,nbPixels+2);

		//Creating the image thanks to the cut
		int[][] img=new int[height][width];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(cut[i*width+j]==1){
					img[i][j]=1;
				} else {
					img[i][j]=0;
				}
			}
		}
		return img;
	}

	private void residualGraphExporation(int cut[], int nbNodes)
    {

        boolean marked[] = new boolean[nbNodes];
        for(int i=0; i<nbNodes; i++)
            marked[i]=false;

        LinkedList<Integer> ll = new LinkedList<Integer>();
        ll.addFirst(source);
        marked[source] = true;
        // BFS
        while (ll.size()!=0)
        {
            int node = ll.removeFirst();
            cut[node]=1;
            int yNode=node%width;
			int xNode=(node-yNode)/width;
            int nbNeighbours=residualGraph[node].length;
            //System.out.println("Pixel "+xNode+" "+yNode);
            for (int neighbour=0; neighbour<nbNeighbours; neighbour++)
            {
               // System.out.println("Voisin "+neighbour+" Arc de valeur"+residualGraph[node][neighbour]);
                if(residualGraph[node][neighbour] > 0){
					int indiceN=0;
					if(node==source || node==sink){
						indiceN=neighbour;
					} else {
						switch(neighbour){
							case 0:
								indiceN=(xNode)*width+(yNode-1);
								break;
							case 1:
								indiceN=(xNode)*width+(yNode+1);
								break;
							case 2:
								indiceN=(xNode-1)*width+yNode;
								break;
							case 3:
								indiceN=(xNode+1)*width+yNode;
								break;
							case 4:
								indiceN=nbNodes-2;
								break;

							case 5:
								indiceN=nbNodes-1;
								break;
							case 6:
								indiceN=(xNode-1)*width+(yNode-1);
								break;
							case 7:
								indiceN=(xNode-1)*width+(yNode+1);
								break;
							case 8:
								indiceN=(xNode+1)*width+(yNode-1);
								break;
							case 9:
								indiceN=(xNode+1)*width+(yNode+1);
								break;
						}
					}
					if (marked[indiceN]==false)
					{
						ll.add(indiceN);
						marked[indiceN] = true;
					}
				}
            }
        }
    }

	private boolean breadth_First_Search(int path[], int nbNodes)
    {

        boolean marked[] = new boolean[nbNodes];
        for(int i=0; i<nbNodes; i++)
            marked[i]=false;

        LinkedList<Integer> ll = new LinkedList<Integer>();
        ll.addFirst(source);
        marked[source] = true;
        path[source]=-1;

        // BFS
        while (ll.size()!=0)
        {
            int node = ll.removeFirst();
            int nbNeighbours=residualGraph[node].length;
            if(node==sink)
				return true;
            for (int neighbour=0; neighbour<nbNeighbours; neighbour++)
            {
                if(residualGraph[node][neighbour] > 0){
					int indiceN=0;
					int yNode=node%width;
					int xNode=(node-yNode)/width;
					if(node==source || node==sink){
						indiceN=neighbour;
					} else {
						switch(neighbour){
								case 0:
									indiceN=(xNode)*width+(yNode-1);
									break;
								case 1:
									indiceN=(xNode)*width+(yNode+1);
									break;
								case 2:
									indiceN=(xNode-1)*width+yNode;
									break;
								case 3:
									indiceN=(xNode+1)*width+yNode;
									break;
								case 4:
									indiceN=nbNodes-2;
									break;
								case 5:
									indiceN=nbNodes-1;
									break;
								case 6:
									indiceN=(xNode-1)*width+(yNode-1);
									break;
								case 7:
									indiceN=(xNode-1)*width+(yNode+1);
									break;
								case 8:
									indiceN=(xNode+1)*width+(yNode-1);
									break;
								case 9:
									indiceN=(xNode+1)*width+(yNode+1);
									break;
						}
					}
					if(indiceN<0)
						System.out.println(xNode+" "+yNode+" "+neighbour+" "+residualGraph[node][neighbour]);
					if (marked[indiceN]==false)
					{
						ll.add(indiceN);
						path[indiceN] = node;
						marked[indiceN] = true;
						//System.out.println("Ajout de "+node+" vers "+neighbour);
					}
				}
            }
        }
        return false;
    }

	private void initResidualGraph(){
		residualGraph=new int[adjMatrix.length][];
		for(int i=0;i<residualGraph.length;i++){
			int length=10;
			if(i>=nbPixels)
				length=nbPixels;
			residualGraph[i]=new int[length];
			for(int j=0;j<length;j++){
				residualGraph[i][j]=adjMatrix[i][j];
			}
		}
	}

}
