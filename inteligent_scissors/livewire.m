function varargout = livewire(I, dRadius, dCaptureRadius)

dPointerCross = NaN(16);
dPointerCross(8   ,1:15) = 2;
dPointerCross(1:15,   8) = 2;

dPointerCrossMag = ...
   [NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ... 
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ... 
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
      2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN,   2, NaN, NaN, NaN,   2, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN,   2, NaN, NaN, NaN,   2, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN,   2, NaN, NaN, NaN,   2, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN,   2, NaN, NaN, NaN,   2, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN,   2, NaN, NaN, NaN,   2,   2,   2, NaN, NaN; ...
    NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN];

% Check if number of output arguments is acceptable.
if nargout == 2 || nargout > 3
    error('LIVEWIRE expects either 0, 2 or 3 output arguments.'); 
end

% Check input argument and try to get a figure/axes/image handle if no
% input image is supplied (operate on current axes).
if ~nargin
    hF = get(0, 'CurrentFigure');
    if isempty(hF), error('Found no figure. Call LIVEWIRE with an image argument!'); end
    
    hA = get(hF, 'CurrentAxes');
    if isempty(hA), error('Found no axes. Call LIVEWIRE with an image argument!'); end
    
    hI = findobj(hA, 'Type', 'image');
    if isempty(hF), error('Found no image object. Call LIVEWIRE with an image argument!'); end
    
    dImg = double(get(hI, 'CData'));
else
    if size(I, 3) == 3, I = rgb2gray(I); end
    dImg = double(I);
    hI = imshow(dImg, []);
    hA = gca;
    hF = gcf;
end

% Define optional input parameters if omitted
if nargin < 2
    SOptions.dRadius = 200;
    SOptions.dCaptureRadius = 4;
end

% Bring figure to the front and set callbacks
figure(hF);
try
    set(hF, ...
        'WindowButtonDownFcn'   , @fButtonDownFcn,...
        'KeyPressFcn'           , @fKeyPressFcn, ...
        'KeyReleaseFcn'         , @fKeyReleaseFcn, ...
        'Pointer'               , 'custom', ...
        'PointerShapeCData'     , dPointerCrossMag, ...
        'PointerShapeHotSpot'   , [8, 8], ...
        'DoubleBuffer'          , 'on'); %'WindowButtonMotionFcn' , @fMotionFcn, ...
catch
    set(hF, ...
        'WindowButtonDownFcn'   , @fButtonDownFcn,...
        'KeyPressFcn'           , @fKeyPressFcn, ...
        'Pointer'               , 'custom', ...
        'PointerShapeCData'     , dPointerCrossMag, ...
        'PointerShapeHotSpot'   , [8, 8], ...
        'DoubleBuffer'          , 'on'); %'WindowButtonMotionFcn' , @fMotionFcn, ...
end

% Create the line objects (use two lines for better visibility)
hLine1 = line(...
    'Parent'    , hA, ...
    'XData'     , [], ...
    'YData'     , [], ...
    'Clipping'  , 'off', ...
    'Color'     , 'g', ...
    'LineStyle' , '-', ...
    'LineWidth' , 1.5);

hLine2 = line(...
    'Parent'    , hA, ...
    'XData'     , [], ...
    'YData'     , [], ...
    'Clipping'  , 'off', ...
    'Color'     , 'r', ...
    'LineStyle' , ':', ...
    'LineWidth' , 1.5);

% Initialize the global variables
%dF          = fLiveWireGetCostFcn(dImg); % The cost function of the live-wire algorithm, see Ref.
iPX         = zeros(size(dImg), 'int8'); % The path map that shows the cheapest path to the sast anchor point.
iPY         = zeros(size(dImg), 'int8'); % The path map that shows the cheapest path to the sast anchor point.
dXData      = [];                        % The x-coordinates of the path
dYData      = [];                        % The y-coordinates of the path
iAnchorList = zeros(200, 1);             % A list of the anchor point indices in dXData and dYData for undo operations
iAnchorInd  = 0;                         % The index of the list
lRegularEnd = false;                     % Indicates whether path was successfully drawn or the UI was aborted.
lControl    = false;                     % Indicates whether the control key is pressed

uiwait(hF);

if ~lRegularEnd
    switch nargout

        case 1
            varargout{1} = false(size(dImg));

        case 3
            varargout{1} = false(size(dImg));
            varargout{2} = [];
            varargout{3} = [];

        otherwise

    end
    return
end

% Assign the return values
lMask = poly2mask(dXData, dYData, size(dImg, 1), size(dImg, 2));
switch nargout
    
    case 0
        figure, imshow(lMask)

    case 1
        varargout{1} = lMask;

    case 3
        varargout{1} = lMask;
        varargout{2} = dXData;
        varargout{3} = dYData;

    otherwise
end


    % Executed when the mouse button is pressed. Used to determine a
    % new anchor point or to end the user interaction.
    function fButtonDownFcn(hObject, eventdata) %#ok<*INUSD> eventdata is repeatedly unused
        [dX, dY] = fGetAxesPos;
        if ~dX, return, end
        
        if isempty(dXData)
            % The starting point of the path is selected
            dXData = dX;
            dYData = dY;
        else
            % A new anchor point and the cheapest path to the last anchor
            % point is appended to the path
            [iXPath, iYPath] = fLiveWireGetPath(iPX, iPY, dX, dY);
            if isempty(iXPath)
                iXPath = dX;
                iYPath = dY;
            end
            dXData = [dXData, double(iXPath(:)')];
            dYData = [dYData, double(iYPath(:)')];
        end

        iAnchorInd = iAnchorInd + 1;
        iAnchorList(iAnchorInd) = length(dXData); % Save the previous path length for the undo operation
        
        % Update the UI and calculate the new path map iP
        set([hLine1, hLine2], 'XData', dXData, 'YData', dYData);
        drawnow expose
        [iPX, iPY] = fLiveWireCalcP(dImg, dX, dY, SOptions.dRadius);

        % If right-click, double-click or shift-click occurred, close path
        % and return.
        if ~(strcmp(get(hF, 'SelectionType'), 'normal')) && ~(lControl)
            [iXPath, iYPath] = fLiveWireGetPath(iPX, iPY, dXData(1), dYData(1));
            if isempty(iXPath)
                iXPath = dXData(1);
                iYPath = dYData(1);
            end
            dXData = [dXData, double(iXPath(:)')];
            dYData = [dYData, double(iYPath(:)')];
            set([hLine1, hLine2], 'XData', dXData, 'YData', dYData);
            drawnow expose
            set(hF, 'WindowButtonMotionFcn', '', 'WindowButtonDownFcn', '', 'KeyPressFcn', '');
            lRegularEnd = true;
            uiresume(hF);
        end

    end

    function [dX, dY] = fGetAxesPos()
        dPos  = get(hA, 'CurrentPoint');
        dXLim = get(hA, 'XLim');
        dYLim = get(hA, 'YLim');
        dX = dPos(1, 1);
        dY = dPos(1, 2);
        if (dX < dXLim(1)) || (dX > dXLim(2)) || ...
           (dY < dYLim(1)) || (dY > dYLim(2))
            dX = 0;
            dY = 0;
        end
    end

    function fKeyPressFcn(hObject, eventdata)
        
        if strcmp(eventdata.Key, 'control')
            set(hF, 'PointerShapeCData', dPointerCross);
            lControl = true;
        end
       
        switch eventdata.Key
            % ENTER: close the path and return
            case 'return'
                [iXPath, iYPath] = fLiveWireGetPath(iPX, iPY, dXData(1), dYData(1));
                if isempty(iXPath)
                    iXPath = dXData(1);
                    iYPath = dYData(1);
                end
                dXData = [dXData, double(iXPath(:)')];
                dYData = [dYData, double(iYPath(:)')];
                set([hLine1, hLine2], 'XData', dXData, 'YData', dYData);
                drawnow expose
                set(hF, 'WindowButtonMotionFcn', '', 'WindowButtonDownFcn', '', 'KeyPressFcn', '');
                lRegularEnd = true;
                uiresume(hF);

            % DEL or BACKSPACE: delete path to last anchor
            case {'delete', 'backspace'}
                iAnchorInd = iAnchorInd - 1;
                if ~iAnchorInd
                    return
                end
                
                dXData = dXData(1:iAnchorList(iAnchorInd));
                dYData = dYData(1:iAnchorList(iAnchorInd));
                
                set([hLine1, hLine2], 'XData', dXData, 'YData', dYData);
                drawnow expose
                [iPX, iPY] = fLiveWireCalcP(dImg, dXData(end), dYData(end), SOptions.dRadius);
                fMotionFcn(hObject, []);

            otherwise

        end
    end

    function fKeyReleaseFcn(hObject, eventdata)
        if strcmp(eventdata.Key, 'control')
            lControl = false;
            set(hF, 'PointerShapeCData', dPointerCrossMag);
        end
    end

    % Closes the figure if requested.
    function fCloseGUI(hObject, eventdata) %#ok<DEFNU> <-stupid!
        delete(hObject);
    end

end
