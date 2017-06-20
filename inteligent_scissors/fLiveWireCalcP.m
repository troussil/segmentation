function [iPX, iPY] = fLiveWireCalcP(dImg, iXS, iYS, dRadius)
%FLIVEWIRECALCP Calculates the path maps in a live-wire implementation .

iLISTMAXLENGTH = 10000;

if nargin < 4, dRadius = 10000; end

[iNRows, iNCols] = size(dImg);
iNPixelsToProcess = min([int32(pi.*dRadius.^2), iNRows.*iNCols]);
iNPixelsProcessed = int32(0);

% Initialize live-wire data structures
iLI = zeros(iLISTMAXLENGTH, 3, 'int32');   % The active list coordinates (x, y, lin)
dLG = zeros(iLISTMAXLENGTH, 1);            % The active list weights
iLInd = uint16(0);               % The active list index: Points to the last element of iLI and dLG

lE = false(size(dImg));          % Binary image indicating whether a pixel has peen processed or not
iPX = zeros(size(dImg), 'int8');% Vector field following the minimum cost path from each processed pixel to the seed
iPY = zeros(size(dImg), 'int8');% Vector field following the minimum cost path from each processed pixel to the seed

% Build neighbourhoor index lookup tables for faster loop execution
[iXX, iYY] = meshgrid(1:iNCols, 1:iNRows);
iXX = int16(iXX); iYY = int16(iYY);
iNX = cat(3, iXX - 1, iXX    , iXX + 1, iXX - 1, iXX + 1, iXX - 1, iXX    , iXX + 1);
iNY = cat(3, iYY - 1, iYY - 1, iYY - 1, iYY    , iYY    , iYY + 1, iYY + 1, iYY + 1);
iNX = permute(iNX, [3 2 1]);
iNY = permute(iNY, [3 2 1]);

iDirX = int16([1; 0; -1; 1; -1;  1;  0; -1]);
iDirY = int16([1; 1;  1; 0;  0; -1; -1; -1]);
%dWeight = [1, 0.7, 1, 0.7, 0.7, 1, 0.7, 1];

%to calculate the dImgP and dImgQ(8 neighbors)
dImgP=dImg;
dImgNQ = cat(3,dImg,dImg,dImg,dImg,dImg,dImg,dImg,dImg);
%dImgNQ = permute(dImgNQ, [3 1 2]);
id = repmat({':'}, ndims(dImg), 1);
n = size(dImg, 1);
m = size(dImg, 2);
for i = 1:8
        if iDirX(i) > 0
            id{1} = [2:n 1];
        elseif iDirX(i) < 0
            id{1} = [n 1:n - 1];
        else 
            id{1} =  [1:n];  
        end

        if iDirY(i) > 0
            id{2} = [2:n 1];
        elseif iDirY(i) < 0
            id{2} = [n 1:n - 1];
        else 
            id{2} = [1:n];  
        end    
        dImgNQ(:,:,i)= dImg(id{:});
end
dF = cat(3,dImg,dImg,dImg,dImg,dImg,dImg,dImg,dImg);
%dF = permute(dF, [3 1 2]);
for i = 1:8
    dF(:,:,i) = fLiveWireGetCostFcn(dImgP,dImgNQ(:,:,i));
end
% Initialize active list with zero cost seed pixel.
iLInd = iLInd + 1;
iLI(iLInd, :) = [iXS, iYS, (iXS - 1).*iNRows + iYS];
dLG(iLInd) = 0;

    
% While there are still objects in the active list and pixel limit not
% reached
while (iLInd > 0) && (iNPixelsProcessed < iNPixelsToProcess)
    % Determine pixel q in list with minimal cost and remove from active
    % list. Mark q as processed.
    [temp, iInd] = min(dLG(1:iLInd));
    iQI = iLI(iInd, :);
    dQG = dLG(iInd);
    iLI(iInd, :) = iLI(iLInd, :);
    dLG(iInd, :) = dLG(iLInd, :);
    iLInd = iLInd - 1;
    
    lE(iQI(2), iQI(1)) = true;

    % Generate neighbourhood of q
    iN = zeros(8, 4, 'int16');
    iN(:, 1) = iNX(:, iQI(1), iQI(2));
    iN(:, 2) = iNY(:, iQI(1), iQI(2));
    iN(:, 3) = iDirX;
    iN(:, 4) = iDirY;
    
    % Mask out pixels that are out of bounds (q is a border pixel)
    lValid = (iN(:, 1) > 0) & (iN(:, 1) <= iNCols) & ...
             (iN(:, 2) > 0) & (iN(:, 2) <= iNRows);

    iN = iN(lValid, :);
    %dThisWeight = dWeight(lValid);

    % Loop over the neighbourhood of q
    for iI = 1:size(iN, 1);
        if lE(iN(iI, 2), iN(iI, 1)), continue, end % Skip if neighbourhood pixel already processed
        iR = iN(iI,:);
    
        iLinInd = (int32(iR(2)) - 1).*iNRows + int32(iR(1));
        % Compute the new accumulated cost to the neighbourhood pixel
 
        %dImgP=dImg;
        %id = repmat({':'}, ndims(dImg), 1);
        %n = size(dImg, 1);
       % if iR(3) > 0
       %     id{1} = [2:n 1];
       % elseif iR(3) < 0
       %     id{1} = [n 1:n - 1];
       % else 
       %     id{1} =  [1:n];  
       % end
       % n = size(dImg, 2);
       % if iR(4) > 0
       %     id{2} = [2:n 1];
       % elseif iR(4) < 0
        %    id{2} = [n 1:n - 1];
       % else 
       %     id{2} = [1:n];  
      %  end
        
       % dImgQ= dImg(id{:});
       % dF = fLiveWireGetCostFcn(dImgP,dImgQ);%�˴�ѭ������̫�࣬ʱ�临�Ӷ�̫�ߣ�����ֻ����һ��
        dThisG = dQG + dF(iR(2), iR(1),iI);%.*dThisWeight(iI);
        
        % Check whether r is already in active list and if the
        % current cost is lower than the previous
        iInd = find(iLI(1:iLInd, 3) == iLinInd);
        if ~isempty(iInd)
            if dThisG < dLG(iInd)
                dLG(iInd) = dThisG;
                iPX(iR(2), iR(1)) = iR(3);
                iPY(iR(2), iR(1)) = iR(4);
            end
        else
        % If r is not in the active list, add it!
            iLInd = iLInd + 1;
            iLI(iLInd, :) = [int32(iR(1)), int32(iR(2)), iLinInd];
            dLG(iLInd) = dThisG;

            iPX(iR(2), iR(1)) = iR(3);
            iPY(iR(2), iR(1)) = iR(4);
        end 
        
    end % FOR loop over the neighborhood of q
    
    iNPixelsProcessed = iNPixelsProcessed + 1;
  
end % WHILE
