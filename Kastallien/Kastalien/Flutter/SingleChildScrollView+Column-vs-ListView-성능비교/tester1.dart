// strict_sequence_probe.dart
import 'dart:convert';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';

class _FrameTimingRecorder {
  _FrameTimingRecorder(this.label);
  final String label;
  bool _recording = false;
  final List<FrameTiming> _frames = [];

  void _onTimings(List<FrameTiming> timings) {
    if (!_recording) return;
    _frames.addAll(timings);
  }

  void start() {
    if (_recording) return;
    SchedulerBinding.instance.addTimingsCallback(_onTimings);
    _recording = true;
  }

  Map<String, dynamic> stopAndSummarize() {
    if (!_recording) return {'error': 'not_recording'};
    _recording = false;
    SchedulerBinding.instance.removeTimingsCallback(_onTimings);

    final hz = _detectRefreshRateHz() ?? 60.0;
    final budget = 1000.0 / hz;
    double ms(Duration d) => d.inMicroseconds / 1000.0;

    final build = <double>[];
    final raster = <double>[];
    final worst = <double>[];
    for (final f in _frames) {
      final b = ms(f.buildDuration), r = ms(f.rasterDuration);
      build.add(b); raster.add(r); worst.add(b > r ? b : r);
    }

    Map<String, dynamic> stats(List<double> xs) {
      if (xs.isEmpty) return {'count':0,'avg_ms':0,'p95_ms':0,'p99_ms':0,'max_ms':0,'min_ms':0};
      xs.sort();
      double q(double p){final pos=(xs.length-1)*p;final lo=pos.floor(),hi=pos.ceil();if(lo==hi)return xs[lo];final t=pos-lo;return xs[lo]*(1-t)+xs[hi]*t;}
      double avg = xs.reduce((a,b)=>a+b)/xs.length;
      double r(double v)=>double.parse(v.toStringAsFixed(3));
      return {'count':xs.length,'avg_ms':r(avg),'p95_ms':r(q(0.95)),'p99_ms':r(q(0.99)),'max_ms':r(xs.last),'min_ms':r(xs.first)};
    }

    final missed = worst.where((w) => w > budget).length;
    final total = worst.length;
    double r(double v)=>double.parse(v.toStringAsFixed(3));

    return {
      'label': label,
      'device_refresh_hz': r(hz),
      'frame_budget_ms': r(budget),
      'frames_captured': total,
      'missed_frames': missed,
      'missed_pct': total==0?0.0:r(missed*100.0/total),
      'build': stats(build),
      'raster': stats(raster),
      'worst_phase': stats(worst),
    };
  }

  static double? _detectRefreshRateHz() {
    try {
      final views = PlatformDispatcher.instance.views;
      if (views.isNotEmpty) {
        final hz = views.first.display.refreshRate;
        if (hz != null && hz > 0) return hz;
      }
    } catch (_) {}
    return null;
  }
}

class StrictSequenceProbe extends StatefulWidget {
  const StrictSequenceProbe({
    super.key,
    required this.childBuilder,         // 나타낼(측정할) 리스트/스크롤 뷰
    required this.label,                // 케이스 라벨
    this.delayBeforeRecord = const Duration(seconds: 1),   // 버튼 후 1s
    this.preListRecord = const Duration(milliseconds: 500),// 기록 후 0.5s 대기
    this.postListRecord = const Duration(milliseconds: 500)// 리스트 표시 후 0.5s
  });

  final WidgetBuilder childBuilder;
  final String label;
  final Duration delayBeforeRecord;
  final Duration preListRecord;
  final Duration postListRecord;

  @override
  State<StrictSequenceProbe> createState() => _StrictSequenceProbeState();
}

class _StrictSequenceProbeState extends State<StrictSequenceProbe> {
  bool _showButton = true; // 시작 버튼 노출
  bool _showList = false;  // 리스트 노출
  bool _armed = false;     // 중복 클릭 방지
  late final _FrameTimingRecorder _rec = _FrameTimingRecorder(widget.label);

  Future<void> _runSequence() async {
    if (_armed) return;
    _armed = true;

    // 1) 버튼 사라짐 (측정 전)
    setState(() => _showButton = false);

    // 2) 1초 대기
    await Future.delayed(widget.delayBeforeRecord);

    // 3) 기록 시작
    _rec.start();

    // 4) 0.5초 대기
    await Future.delayed(widget.preListRecord);

    // 5) 리스트 표시 (측정 중에 setState 1회)
    setState(() => _showList = true);

    // 6) 0.5초 대기
    await Future.delayed(widget.postListRecord);

    // 7) 기록 종료 + 로그 출력
    final summary = _rec.stopAndSummarize();
    // ignore: avoid_print
    print('📊 StrictSequenceProbe[${widget.label}] ${jsonEncode(summary)}');
  }

  @override
  Widget build(BuildContext context) {
    if (_showButton) {
      // 최소한의 버튼(리플 효과 등 부가 애니 없음)
      return Center(
        child: TextButton(onPressed: _runSequence, child: const Text('측정 시작')),
      );
    }
    // 버튼은 사라진 상태
    return _showList ? widget.childBuilder(context) : const SizedBox.shrink();
  }
}
