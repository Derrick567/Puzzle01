# Puzzle01


重點

1. onMeasure()的使用



2.傳入多個參數 ，取得最小的一個

    private int min(int... params) {
    
        int min = params[0];
        
        for (int param : params) {
        
            if (param < min)
            
                min = param;
        }
        
        return min;
    }
    
    
3. 使用自訂sort 完成亂序

        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {

            @Override
            
            public int compare(ImagePiece a, ImagePiece b) {

                return Math.random() > 0.5 ? 1 : -1;
            }
        });
